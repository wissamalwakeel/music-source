package com.musicsource.interfaces;

import com.musicsource.models.Album;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * mongoDB album repository to cache album data collected from different sites.
 */
public interface AlbumRepository extends MongoRepository<Album, String> {

    Album findAlbumById(String id);

    Album insert(Album album);
}
