package com.musicsource.services;


import com.musicsource.constants.Constants;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.musicbrainz.webservice.WebService;
import org.musicbrainz.webservice.WebServiceException;
import org.musicbrainz.webservice.impl.HttpClientWebServiceWs2;
import org.musicbrainz.wsxml.MbXMLException;
import org.musicbrainz.wsxml.element.Metadata;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
@Qualifier("HttpClientWebServiceWs2.")
public class MusicBrainzService {

    private static final Logger LOGGER = LogManager.getLogger(MusicBrainzService.class);

    private static Map<String, String> filters = new HashMap<>();

    private final WebService webService;

    public MusicBrainzService(WebService webService) {
        filters.put("inc", Constants.MUSICBRAINZ_FILTERS);
        this.webService = webService;
    }

    public MusicBrainzService() {
        filters = new HashMap<>();
        filters.put("inc", Constants.MUSICBRAINZ_FILTERS);
        webService = new HttpClientWebServiceWs2();
    }

    /**
     * Retrive the Artist/Band data from MusicBrainz api
     *
     * @param mbid String containing the MBID of the artist from MusicBrainz in UUID format
     * @return Metadata Object containing the response from MusicBrainz api
     */
    public Metadata getArtistDataByMbId(String mbid) {
        Metadata response = new Metadata();
        try {
            LOGGER.log(Level.DEBUG, "Retrieving Artist Data from MusicBrainz service for mbid:{} ", mbid);
            response = getArtistMetadata(mbid, webService);
            LOGGER.log(Level.INFO, MessageFormat.format("Retrieved Artist Data for mbid: {0} and artist name: {1}", mbid, response.getArtistWs2()));
        } catch (WebServiceException | MbXMLException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
        }
        return response;
    }

    private Metadata getArtistMetadata(String mbid, WebService webService) throws WebServiceException, MbXMLException {
        return webService.get("artist", mbid, null, filters);
    }
}
