package com.musicsource.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicsource.models.WikiPageData;
import com.musicsource.models.WikiResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fastily.jwiki.core.Wiki;
import org.musicbrainz.model.RelationWs2;
import org.musicbrainz.wsxml.element.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

@Service
public class WikipediaService {

    private static final Logger LOGGER = LogManager.getLogger(WikipediaService.class);
    private final Wiki wiki;
    private final ObjectMapper objectMapper;

    public WikipediaService(@Value("${wikipedia.base.host:en.wikipedia.org}") String wikipediaBaseHost,
                            @Value("${wikipedia.user}") String user,
                            @Value("${wikipedia.password}") String password,
                            @Value("${wikipedia.schema}") String schema) {
        wiki = new Wiki.Builder().withApiEndpoint(new HttpUrl.Builder()
                .host(wikipediaBaseHost)
                .addPathSegment("w")
                .addPathSegment("api.php")
                .addQueryParameter("format", "json")
                .addQueryParameter("prop", "extracts")
                .addQueryParameter("exintro", "true")
                .addQueryParameter("redirects", "true")
                .username(user)
                .password(password)
                .scheme(schema)
                .build()).build();
        objectMapper = new ObjectMapper();
    }

    /**
     * Get the description fro wikipedia and getting the title for that fro wikidata
     *
     * @param artistData MetaData Object from MusicBrainz containing artist/band data
     * @return String containing the description of the artist/band
     */
    public String getDescriptionFromWikipedia(Metadata artistData) {
        String titles = getTitle(getWikiDataRelation(artistData.getArtistWs2().getRelationList().getRelations()));
        Response response = wiki.basicGET("query", "titles", titles);
        LOGGER.log(Level.INFO, "Queried description from wikipedia for title {}", titles);
        WikiResponse wikiResponse = getWikiResponse(titles, response);
        return getCleanDescription(wikiResponse);
    }

    private synchronized String getCleanDescription(WikiResponse wikiResponse) { ;
        return ((WikiPageData) ((Map.Entry) wikiResponse.getQuery().getPages().entrySet().toArray()[0]).getValue())
                .getExtract().replaceAll("(<p class=\\\"mw-empty-elt\\\">\\n+<\\/p>\\n)|(<!--[\\s\\S]*?-->)|(\\n+)", "");
    }

    /**
     * Retrive the title of the artist/band in wikipedia from wikidata
     *
     * @param wikiDataRelation a RelationWs2 Object containing the Relation to  wikidata
     * @return String containing the wikipedia title of the artist/band
     */
    private String getTitle(RelationWs2 wikiDataRelation) {
        if (wikiDataRelation != null) {
            String wikiDataId = wikiDataRelation.getTargetId().replace("https://www.wikidata.org/wiki/", "");
            try {
                ApiConnection apiConnection = new BasicApiConnection("https://www.wikidata.org/w/api.php");
                Map<String, String> parameters = getWikiDataRequestParameters(wikiDataId);
                JsonNode response = apiConnection.sendJsonRequest("GET", parameters);
                return response.get("entities").get(wikiDataId).get("sitelinks").get("enwiki").get("title").textValue();
            } catch (MediaWikiApiErrorException | IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());;
            }
        }
        return "";
    }

    /**
     * Retrive the wikidata Relation from the list of available relations.
     *
     * @param relationsList a List<RelationWs2> containing the list of relations from MusicBrainz response
     * @return RelationWs2 Object containing the relation to wikidata
     */
    private RelationWs2 getWikiDataRelation(List<RelationWs2> relationsList) {
        return relationsList.stream().filter(relationWs2 ->
                relationWs2.getTargetId().contains("https://www.wikidata.org/wiki"))
                .findFirst().orElse(null);
    }

    /**
     * Build request parameters for wikidata request and including the wikiDataId
     *
     * @param wikiDataId String containing the wikidata ID
     * @return a Map<String, String> containing the parameters for the request.
     */
    private Map<String, String> getWikiDataRequestParameters(String wikiDataId) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("action", "wbgetentities");
        parameters.put("ids", wikiDataId);
        parameters.put("props", "info|datatype|labels|aliases|descriptions|claims|sitelinks");
        return parameters;
    }

    /**
     * Maps the wikipedia response body to our WikiResponse object, this should include the description if found.
     *
     * @param title String containing the title for the artist/band from wikipedia
     * @param response Response Object from wikipedia api
     * @return WikiResponse Object containing the description if found.
     */
    private WikiResponse getWikiResponse(String title,  Response response) {
        WikiResponse wikiResponse = new WikiResponse();
        String responseBody;
        try {
            responseBody = response.body() != null ? response.body().string() : "";
            if (StringUtils.isNotBlank(responseBody)) {
                wikiResponse = objectMapper.readValue(responseBody.getBytes(), WikiResponse.class);
                LOGGER.log(Level.INFO, MessageFormat.format("Queried description from wikipedia for {0}", title));
            } else {
                LOGGER.log(Level.INFO, MessageFormat.format("Unable to get description from wikipedia for {0}", title));
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
        }
        return wikiResponse;
    }
}
