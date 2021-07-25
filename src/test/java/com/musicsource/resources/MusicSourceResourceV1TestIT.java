package com.musicsource.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicsource.app.MusicSourceApplication;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,classes = MusicSourceApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MusicSourceResourceV1TestIT {
    // I would use wireMock or mockServer to simulate external systems but some of the clients have a built-in hostname
    // configuration other option would be to use Feign Request Interceptor or OpenFeign to intercept the request and
    // mock the response, I only used Feign and mockServer once for each, for the mockServer I used a standalone version
    // for testing multiple services with predefined templates and works as a proxy as well.  

    @SpyBean
    WikipediaService wikipediaService;

    @SpyBean
    MusicBrainzService musicBrainzService;

    @SpyBean
    CoverArtArchiveService coverArtArchiveService;

    @Autowired
    MusicSourceResourceV1 musicSourceResourceV1;

    private MusicSourceResponse musicSourceResponse;

    @Before
    public void init() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        musicSourceResponse = objectMapper
                .readValue(new File("src/test/resources/test-data/music-source-nirvana-response.json"),
                        MusicSourceResponse.class);
    }

    @Test
    public void getAlbumInfoByMbIdSuccessIT() {
        MusicSourceResponse response = musicSourceResourceV1.getAlbumInfoByMbId("5b11f4ce-a62d-471e-81fc-a69a8278c7da");
        Assert.assertNotNull(response);
        Assert.assertEquals(musicSourceResponse.getDescription(), response.getDescription());
    }
}
