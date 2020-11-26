package com.mhk.crawler.model;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ToonTitleDetail {
    private ToonService service;
    private String name;
    private String genre;
    private String keyword;
    private String url;
    private String key;
    private LocalDate date;
    private Double avgRating;
    private Integer totalVote;
    private String etc;
}
