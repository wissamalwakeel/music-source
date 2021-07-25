package com.musicsource.models;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Album {
    // TTL for controlling cached data live can be use as an integer as well but then I would create to models one for
    // caching and one for the response object to avoid sending unrequited internal data.
    @Id
    private String id;

    private String title;

    private String image;

    public Album() {}

    public Album(String id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }

    @Override
    public String toString() {
        return "Album{'id': '"+ id + "', 'title':'" + title + "', 'image': '" + image + "'}";
    }
}
