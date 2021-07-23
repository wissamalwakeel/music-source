package com.musicsource.models;

import lombok.Data;

@Data
public class WikiResponse {
    private String batchcomplete;
    private WikiWarning warnings;
    private WikiQuery query;
}
