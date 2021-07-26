package com.musicsource.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicsource.models.MusicSourceResponse;
import com.musicsource.services.CoverArtArchiveService;
import com.musicsource.services.MusicBrainzService;
import com.musicsource.services.WikipediaService;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.musicbrainz.wsxml.element.Metadata;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MusicSourceResourceV1Test {
    // I would use wireMock or mockServer to simulate external systems but some of the clients have a built-in hostname
    // configuration other option would be to use Feign Request Interceptor or OpenFeign to intercept the request and
    // mock the response, I only used Feign and mockServer once for each, for the mockServer I used a standalone version
    // for testing multiple services with predefined templates and works as a proxy as well.

    @Mock
    private WikipediaService wikipediaService;

    @Mock
    private MusicBrainzService musicBrainzService;

    @Mock
    private CoverArtArchiveService coverArtArchiveService;

    @InjectMocks
    private MusicSourceResourceV1 musicSourceResourceV1;

    @Mock
    private Metadata mockedMetadata;

    private MusicSourceResponse expectedMusicSourceResponse;

    @Before
    public void init() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        expectedMusicSourceResponse = objectMapper
                .readValue(new File("src/test/resources/test-data/music-source-nirvana-response.json"),
                        MusicSourceResponse.class);
        when(musicBrainzService.getArtistDataByMbId(expectedMusicSourceResponse.getMbid())).thenReturn(mockedMetadata);
        when(wikipediaService.getDescriptionFromWikipedia(mockedMetadata)).thenReturn(expectedMusicSourceResponse.getDescription());
        when(coverArtArchiveService.getAlbums(mockedMetadata)).thenReturn(expectedMusicSourceResponse.getAlbums());
    }

    @Test
    public void getAlbumInfoByMbIdSuccessIT() {
        MusicSourceResponse response = musicSourceResourceV1.getAlbumInfoByMbId(expectedMusicSourceResponse.getMbid());
        Assert.assertNotNull(response);
        Assert.assertEquals(expectedMusicSourceResponse.getDescription(), response.getDescription());
        Assert.assertEquals(expectedMusicSourceResponse.getMbid(), response.getMbid());
        expectedMusicSourceResponse.getAlbums().forEach(album -> Assert.assertTrue(response.getAlbums().contains(album)));
    }
}
