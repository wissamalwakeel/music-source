package com.musicsource.resources;

import com.musicsource.models.Album;
import com.musicsource.models.MusicSourceResponse;
import com.musicsource.services.CoverArtArchiveService;
import com.musicsource.services.MusicBrainzService;
import com.musicsource.services.WikipediaService;
import java.util.List;
import org.musicbrainz.wsxml.element.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
    Resource class used to call different services and collect data and provide response.
 */
@Component
public class MusicSourceResourceV1 {

    @Autowired
    WikipediaService wikipediaService;

    @Autowired
    MusicBrainzService musicBrainzService;

    @Autowired
    CoverArtArchiveService coverArtArchiveService;

    /**
     * uses the different serivce classes to collect data from remote systems and return the response object
     *
     * @param mbid String representing the UUID of the artist/band to use to get data from MusicBrainz
     * @return MusicSourceResponse containing mbid, artist description from wikipedia, and list of Album objects
     */
    public MusicSourceResponse getAlbumInfoByMbId(String mbid) {
        Metadata artistData = musicBrainzService.getArtistDataByMbId(mbid);
        String wikiDescription = wikipediaService.getDescriptionFromWikipedia(artistData);
        return buildResponse(mbid, wikiDescription, coverArtArchiveService.getAlbums(artistData));
    }

    /**
     *
     * @param mbid String representing the UUID of the artist/band
     * @param wikiDescription String containing the description extracted from wikipedia
     * @param albums a list of Album objects containing the albums released by the artist/band
     * @return MusicSourceResponse combining the above data into one object.
     */
    private MusicSourceResponse buildResponse(String mbid, String wikiDescription, List<Album> albums) {
        MusicSourceResponse musicSourceResponse = new MusicSourceResponse();
        musicSourceResponse.setMbid(mbid);
        musicSourceResponse.setDescription(wikiDescription);
        musicSourceResponse.setAlbums(albums);
        return musicSourceResponse;
    }
}
