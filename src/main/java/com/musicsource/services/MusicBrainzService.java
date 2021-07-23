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
import org.springframework.stereotype.Service;

@Service
public class MusicBrainzService {

    private static final Logger LOGGER = LogManager.getLogger(MusicBrainzService.class);

    private static Map<String, String> filters;

    public MusicBrainzService() {
        filters = new HashMap<>();
        filters.put("inc", Constants.MUSICBRAINZ_FILTERS);
    }

    public Metadata getArtistDataByMbId(String mbid) {
        Metadata response = new Metadata();
        WebService webService = new HttpClientWebServiceWs2();
        try {
            LOGGER.log(Level.DEBUG, "Retrieving Artist Data from MusicBrainz service for mbid:{} ", mbid);
            response = webService.get("artist", mbid, null, filters);
            LOGGER.log(Level.INFO, MessageFormat.format("Retrieved Artist Data for mbid: {0} and artist name: {1}", mbid, response.getArtistWs2()));
        } catch (WebServiceException | MbXMLException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        return response;
    }
}
