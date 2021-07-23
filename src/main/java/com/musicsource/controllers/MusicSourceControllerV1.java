package com.musicsource.controllers;

import com.musicsource.resources.MusicSourceResourceV1;
import com.musicsource.models.MusicSourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/musicsource/")
public class MusicSourceControllerV1 {

    @Autowired
    MusicSourceResourceV1 musicSourceResource;

    /**
     * @param mbId path parameter String representing the UUID of the artist/band from MusicBrainz
     * @return Json string including the mbid and a description of the artist/band from wikipedia and a list of albums released with cover image links from "Cover Art
     * Archive" and ids from "MusicBrainz"
     */
    @GetMapping
    @RequestMapping("/{mbid}")
    public MusicSourceResponse getAlbumInfoByMbId(@PathVariable("mbid") String mbId) {
        return musicSourceResource.getAlbumInfoByMbId(mbId);
    }
}
