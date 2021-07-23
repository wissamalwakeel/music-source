package com.musicsource.models;

import java.util.Map;
import lombok.Data;

@Data
public class WikiQuery {
    Map<String, WikiPageData> pages;
}
