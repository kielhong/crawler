package com.mhk.crawler.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.StringJoiner;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ToonEpisode {
    private ToonTitle title;
    private String name;
    private String url;
    private LocalDate date;
    private DayOfWeek day;
    private Float rating;
    private Integer vote;
    private boolean free;

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",")
                .add(this.getTitle().getName().replace(",", ""))
                .add(this.getName())
                .add(this.getDate().toString())
                .add(this.getDay() != null ? this.getDay().toString() : "FINISHED");
        if (title.getService() == ToonService.NAVER) {
            joiner.add(this.getRating().toString());
            joiner.add(this.getVote().toString());
        } else if (title.getService() == ToonService.DAUM) {
            joiner.add(this.getVote().toString());
            joiner.add(this.isFree() ? "free" : "preview");
        }
        return joiner.toString();
    }
}
