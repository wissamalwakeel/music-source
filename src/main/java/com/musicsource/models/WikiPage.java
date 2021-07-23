package com.musicsource.models;

import java.util.Map;
import lombok.Data;

@Data
public class WikiPage {
    private Map<String, WikiPageData> wikiPageDataMap;
}
