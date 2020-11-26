package com.mhk.crawler.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ToonTitle {
    private ToonService service;
    private String name;
    private String url;
    private String key;
}
