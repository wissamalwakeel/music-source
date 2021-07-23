package com.musicsource.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WikiPageData {
    @JsonProperty(value = "pageid")
    private int pageid;
    private int ns;
    private String title;
    private String extract;
}
