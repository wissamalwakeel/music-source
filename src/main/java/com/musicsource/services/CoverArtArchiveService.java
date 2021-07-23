package com.musicsource.services;

import com.musicsource.constants.Constants;
import com.musicsource.interfaces.AlbumRepository;
import com.musicsource.models.Album;
import fm.last.musicbrainz.coverart.CoverArt;
import fm.last.musicbrainz.coverart.CoverArtArchiveClient;
import fm.last.musicbrainz.coverart.impl.DefaultCoverArtArchiveClient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.musicbrainz.model.entity.ReleaseGroupWs2;
import org.musicbrainz.wsxml.element.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

@Service
@EnableCaching
public class CoverArtArchiveService {

    @Autowired
    private AlbumRepository albumRepository;

    /**
     * Iterate through the list of albums and request the image for each album and collect the ALbum/s into a list that
     * is returned to the caller
     *
     * @param artistData the MusicBrainz response object containing the artist/band data
     * @return a List of Album objects
     */
    public List<Album> getAlbums(Metadata artistData) {
        List<Album> albums = new ArrayList<>();
        CoverArtArchiveClient client = new DefaultCoverArtArchiveClient();
        for (ReleaseGroupWs2 releaseGroup : artistData.getArtistWs2().getReleaseGroupList().getReleaseGroups()) {
            albums.add(getReleaseGroupByMbid(client, releaseGroup.getId(), releaseGroup.getTitle()));
        }
        return albums;
    }

    /**
     * Check the mongoDB if we already cached the album data, if so it return the cached version. if no
     * data found in mongoDB we call the CovertArtArchive api to get the data for the given albumData and use it
     * to build an Album object and cache in DB before returning it
     *
     * @param client CoverArtArchiveClient that has a live connection to CoverArtArchive api
     * @param albumId String representing the Album mbid used to retrieve the cover image data
     * @param title String representing the title of the album.
     * @return
     */
    public Album getReleaseGroupByMbid(CoverArtArchiveClient client, String albumId, String title) {
        Album album = albumRepository.findAlbumById(albumId);
        if (album != null && StringUtils.isNotBlank(album.getImage())) {
            return album;
        }
        CoverArt coverArt = client.getReleaseGroupByMbid(UUID.fromString(albumId));
        Album newAlbum = new Album(albumId, title, coverArt!=null ? coverArt.getFrontImage() != null ? coverArt.getFrontImage().getImageUrl() : Constants.IMAGE_NOT_FOUND : Constants.IMAGE_NOT_FOUND);
        albumRepository.insert(newAlbum);
        return newAlbum;
    }
}
