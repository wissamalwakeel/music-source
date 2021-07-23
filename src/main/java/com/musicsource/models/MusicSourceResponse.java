package com.musicsource.models;

import java.util.List;
import lombok.Data;

@Data
public class MusicSourceResponse {
    private String mbid;
    private String description;
    private List<Album> albums;

}
