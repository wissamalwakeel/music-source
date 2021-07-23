package com.musicsource.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicsource.models.WikiPageData;
import com.musicsource.models.WikiResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.Response;
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







    Wiki wiki;
    ObjectMapper objectMapper;

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

    public String getWikiData(Metadata artistData) {
        String titles = getTitle(artistData);
        List<RelationWs2> relations = artistData.getArtistWs2().getRelationList().getRelations();
        String responseBody = "";
        WikiResponse wikiResponse = null;
        Response response = wiki.basicGET("query", "titles", titles);
        LOGGER.log(Level.INFO, "Queried description from wikipedia for title {}", titles);
        wikiResponse = getWikiResponse(titles, response);
        return ((WikiPageData) ((Map.Entry) wikiResponse.getQuery().getPages().entrySet().toArray()[0]).getValue()).getExtract().replaceAll("<!--[\\s\\S]*?-->", "");
    }

    private String getTitle(Metadata artistData) {
        RelationWs2 wikiDataRelation = artistData.getArtistWs2().getRelationList().getRelations().stream().filter(relationWs2 ->
                relationWs2.getTargetId().contains("https://www.wikidata.org/wiki"))
                .findFirst().orElse(null);
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

        return null;
    }

    private Map<String, String> getWikiDataRequestParameters(String wikiDataId) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("action", "wbgetentities");
        parameters.put("ids", wikiDataId);
        parameters.put("props", "info|datatype|labels|aliases|descriptions|claims|sitelinks");
        return parameters;
    }

    private WikiResponse getWikiResponse(String titles,  Response response) {
        WikiResponse wikiResponse = new WikiResponse();
        String responseBody;
        try {
            responseBody = response.body() != null ? response.body().string() : null;
            assert responseBody != null;
            wikiResponse = objectMapper.readValue(responseBody.getBytes(), WikiResponse.class);
            LOGGER.log(Level.DEBUG, "Queried description from wikipedia for title {0}", titles);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        return wikiResponse;
    }
}
