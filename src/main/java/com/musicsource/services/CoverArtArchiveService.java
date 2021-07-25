package com.musicsource.services;

import com.musicsource.constants.Constants;
import com.musicsource.interfaces.AlbumRepository;
import com.musicsource.models.Album;
import fm.last.musicbrainz.coverart.CoverArt;
import fm.last.musicbrainz.coverart.impl.DefaultCoverArtArchiveClient;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.musicbrainz.wsxml.element.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

@Service
@EnableCaching
public class CoverArtArchiveService {
    private static final Logger LOGGER = LogManager.getLogger(CoverArtArchiveService.class);

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
        artistData.getArtistWs2().getReleaseGroupList().getReleaseGroups().stream().parallel().forEach(releaseGroup -> {
            LOGGER.log(Level.DEBUG, MessageFormat.format("Trying retrieving image from cache or Cover Art " +
                            "Archive service, for album with mbid: {0}, And title: {1}",
                    releaseGroup.getId(), releaseGroup.getTitle()));

            albums.add(getReleaseGroupByMbid(releaseGroup.getId(), releaseGroup.getTitle()));
        });
        return albums;
    }

    /**
     * Check the mongoDB if we already cached the album data, if so it return the cached version. if no
     * data found in mongoDB we call call a diffrent method to retrive the data from the
     *
     * @param albumId String representing the Album mbid used to retrieve the cover image data
     * @param title String representing the title of the album.
     * @return
     */
    public Album getReleaseGroupByMbid(String albumId, String title) {
        Album album = albumRepository.findAlbumById(albumId);
        if (album != null && StringUtils.isNotBlank(album.getImage())) {
            LOGGER.log(Level.INFO, MessageFormat.format("Retrieving image from cache, " +
                            "for album with mbid: {0} , And title: {1}", albumId, title));
            return album;
        }
        return getAlbumFromCoverArt(albumId, title);
    }

    /**
     * Try to retrive the cove image from Cover Art Archive api.
     *
     * @param albumId String containing album UUID
     * @param title String containing album title
     * @return Album object with Album ID and Album title and cover image URL if found.
     */
    private Album getAlbumFromCoverArt(String albumId, String title) {
        CoverArt coverArt = getCoverArt(albumId);
        Album newAlbum = new Album(albumId, title, getImage(coverArt));
        LOGGER.log(Level.INFO, MessageFormat.format("Retrieving image from Cover Art Archive service, " +
                "for album with mbid: {0} , And title: {1}", albumId, title));
        validateAndInsertToDb(newAlbum);
        return newAlbum;
    }

    private CoverArt getCoverArt(String albumId) {
        return new DefaultCoverArtArchiveClient().getReleaseGroupByMbid(UUID.fromString(albumId));
    }

    /**
     * Validate the image url is not emtpy string or null or with value "Image Not Found" and then save to mongoDB
     *
     * @param newAlbum Album object
     */
    private void validateAndInsertToDb(Album newAlbum) {
        if (StringUtils.isNotBlank(newAlbum.getImage()) && !Constants.IMAGE_NOT_FOUND.equals(newAlbum.getImage())) {
            albumRepository.insert(newAlbum);
        }
    }

    /**
     * Extruct the Image from the Cover Art Archive response if exist. else it will return "Image Not Found"
     *
     * @param coverArt CoverArt object containing the response from Cover Art Archive api
     * @return String containing cover image url if found else "Image Not Found"
     */
    private String getImage(CoverArt coverArt) {
        return coverArt != null ? coverArt.getFrontImage() != null ?
                coverArt.getFrontImage().getImageUrl() : Constants.IMAGE_NOT_FOUND : Constants.IMAGE_NOT_FOUND;
    }
}
