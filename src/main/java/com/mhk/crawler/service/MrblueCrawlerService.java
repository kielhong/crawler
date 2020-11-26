package com.mhk.crawler.service;

import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitleDetail;
import com.mhk.crawler.utl.DocUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MrblueCrawlerService {
    private final static String BASE_URL = "http://m.mrblue.com";
    private final static String WEEKLY_TITLE_URL = BASE_URL + "/webtoon/weekday/";
    private final static String TITLE_DETAIL_URL = BASE_URL + "/webtoon/episode/toon/";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public List<ToonTitleDetail> listTitleInfo() {
        List<ToonTitleDetail> titles = new ArrayList<>();
        titles.addAll(listWeeklyTitles());
        titles.addAll(listGenreTitles());
        titles.addAll(listCompletedTitles());

        return titles.stream()
                .distinct()
                .map(this::getTitleDetail)
                .collect(Collectors.toList());
    }

    private List<ToonTitleDetail> listWeeklyTitles() {
        Document doc = DocUtil.readFromUrl(BASE_URL + "/webtoon/list.asp?cycle=all");

        return doc.select("li").stream()
                .map(e -> ToonTitleDetail.builder()
                        .service(ToonService.MRBLUE)
                        .key(e.select("a").first().attr("href").split("=")[1])
                        .url(BASE_URL + e.select("a").first().attr("href"))
                        .name(e.select(".text-subject").text())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ToonTitleDetail> listGenreTitles() {
        Document doc = DocUtil.readFromUrl(BASE_URL + "/webtoon/genre/");

        return doc.select("ul.type-webtoon-list").select("li").stream()
                .map(e -> ToonTitleDetail.builder()
                        .service(ToonService.MRBLUE)
                        .key(e.select("a").first().attr("href").split("=")[1])
                        .url(BASE_URL + e.select("a").first().attr("href"))
                        .name(e.select(".text-subject").text())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ToonTitleDetail> listCompletedTitles() {
        Document doc = DocUtil.readFromUrl(BASE_URL + "/webtoon/completed/");

        return doc.select("ul.type-webtoon-list").select("li").stream()
                .map(e -> ToonTitleDetail.builder()
                        .service(ToonService.MRBLUE)
                        .key(e.select("a").first().attr("href").split("=")[1])
                        .url(BASE_URL + e.select("a").first().attr("href"))
                        .name(e.select(".text-subject").text())
                        .build())
                .collect(Collectors.toList());
    }

    private ToonTitleDetail getTitleDetail(ToonTitleDetail title) {
        Document doc = DocUtil.readFromUrl(title.getUrl());

        System.out.println(title.getUrl());

        title.setGenre(doc.select("ul.summary > li").get(2).select("span").text().split("\\|")[0].trim());
        if (doc.select("ul.summary > li").size() > 4) {
            title.setAvgRating(Double.valueOf(doc.select("ul.summary > li").get(4).select("p.star-base").toString().replaceAll("\\D+","")));
        } else {
            title.setAvgRating(0d);
        }
        title.setDate(LocalDate.parse(doc.select("tr#vols-index-00000").select("span.text-author").text().substring(0, 10), formatter));

        System.out.println(title);
        return title;
    }
}
