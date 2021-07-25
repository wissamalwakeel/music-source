package com.musicsource.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.musicbrainz.webservice.WebServiceException;
import org.musicbrainz.webservice.impl.HttpClientWebServiceWs2;
import org.musicbrainz.wsxml.MbXMLException;
import org.musicbrainz.wsxml.element.Metadata;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MusicBrainzServiceTest {

    @Spy
    HttpClientWebServiceWs2 webService;

    @Mock
    Metadata metadata;

    @Test
    public void getArtistDataByMbIdSuccess() throws WebServiceException, MbXMLException {
        MusicBrainzService musicBrainzService = new MusicBrainzService(webService);
        doReturn(metadata).when(webService).get(anyString(), anyString(), any(), any());
        musicBrainzService.getArtistDataByMbId("TestMBID");
        verify(webService, times(1)).get(anyString(), anyString(), any(), any());
    }

    @Test(expected = MbXMLException.class)
    public void getArtistDataByMbIdThrowException() throws  WebServiceException, MbXMLException {
        MusicBrainzService musicBrainzService = new MusicBrainzService(webService);
        when(webService.get(anyString(), anyString(), any(), any())).thenThrow(MbXMLException.class);
        musicBrainzService.getArtistDataByMbId("TestMBID");
    }
}
