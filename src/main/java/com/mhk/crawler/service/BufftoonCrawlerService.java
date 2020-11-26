package com.mhk.crawler.service;

import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitle;
import com.mhk.crawler.model.ToonTitleDetail;
import com.mhk.crawler.utl.DocUtil;
import com.mhk.crawler.utl.JsonUtil;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

public class BufftoonCrawlerService {
    private final static String BASE_URL = "https://bufftoon.plaync.com";
    private final static String WEEKLY_TITLE_URL = "https://api-bufftoon.plaync.com/v1/contents/webtoon?categoryIdx=0&displaySeriesType=1&offset=0&limit=100&version=2";
    private final static String COMPLETE_TITLE_URL = "https://api-bufftoon.plaync.com/v1/contents/webtoon?categoryIdx=1024&displaySeriesType=2&offset=0&limit=300&version=2";
    private final static String WAITFREE_TITLE_URL = "https://api-bufftoon.plaync.com/v1/contents/webtoon?categoryIdx=1061&displaySeriesType=3&offset=0&limit=300&version=2";
    private final static String TITLE_DETAIL_URL = BASE_URL + "/series";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public List<ToonTitleDetail> listTitleInfo() {
        List<ToonTitleDetail> list = new ArrayList<>();
        list.addAll(listWeeklyTitles());
        list.addAll(listCompletedTitles());
        list.addAll(listWaitFreeTitles());

        return list.stream()
                .sorted(Comparator.comparing(ToonTitleDetail::getKey))
                .distinct()
                .map(t -> getDetail(t))
                .collect(Collectors.toList());
    }

    private List<ToonTitleDetail> listWeeklyTitles() {
        JSONObject jsonObject = JsonUtil.readFromUrl(WEEKLY_TITLE_URL);
        JSONArray contents = jsonObject.getJSONObject("result").getJSONArray("contents");

        return StreamSupport.stream(contents.spliterator(), false)
                .map(e -> (JSONObject)e)
                .map(e -> listWeekTitles(e))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<ToonTitleDetail> listCompletedTitles() {
        JSONObject jsonObject = JsonUtil.readFromUrl(COMPLETE_TITLE_URL);
        JSONArray contents = jsonObject.getJSONObject("result").getJSONArray("contents");

        return listContents(contents);
//        return StreamSupport.stream(contents.spliterator(), false)
//                .map(e -> (JSONObject)e)
//                .map(o -> ToonTitleDetail.builder()
//                        .service(ToonService.BUFTOON)
//                        .key(String.valueOf(o.getInt("seriesIdx")))
//                        .url(BASE_URL + "/series/" + String.valueOf(o.getInt("seriesIdx")))
//                        .name(o.getString("title"))
//                        .genre(toGenre(o.getJSONArray("genres")))
//                        .date(LocalDateTime.ofInstant(Instant.ofEpochMilli(o.getLong("firstEpisodePublishDt")), ZoneId.systemDefault()).toLocalDate())
//                        .build())
//                .collect(Collectors.toList());
    }

    private List<ToonTitleDetail> listWaitFreeTitles() {
        JSONObject jsonObject = JsonUtil.readFromUrl(WAITFREE_TITLE_URL);
        JSONArray contents = jsonObject.getJSONObject("result").getJSONArray("contents");

        return listContents(contents);
    }

    private List<ToonTitleDetail> listWeekTitles(JSONObject jsonObject) {
        JSONArray items = jsonObject.getJSONArray("items");

        return listContents(items);
//        return StreamSupport.stream(items.spliterator(), false)
//                .map(e -> (JSONObject)e)
//                .map(o -> ToonTitleDetail.builder()
//                        .service(ToonService.BUFTOON)
//                        .key(String.valueOf(o.getInt("seriesIdx")))
//                        .url(BASE_URL + "/series/" + String.valueOf(o.getInt("seriesIdx")))
//                        .name(o.getString("title"))
//                        .genre(toGenre(o.getJSONArray("genres")))
//                        .date(LocalDateTime.ofInstant(Instant.ofEpochMilli(o.getLong("firstEpisodePublishDt")), ZoneId.systemDefault()).toLocalDate())
//                        .build())
//                .collect(Collectors.toList());
    }

    private List<ToonTitleDetail> listContents(JSONArray array) {
        return StreamSupport.stream(array.spliterator(), false)
                .map(e -> (JSONObject)e)
                .map(o -> ToonTitleDetail.builder()
                        .service(ToonService.BUFTOON)
                        .key(String.valueOf(o.getInt("seriesIdx")))
                        .url(BASE_URL + "/series/" + String.valueOf(o.getInt("seriesIdx")))
                        .name(o.getString("title"))
                        .genre(toGenre(o.getJSONArray("genres")))
                        .date(LocalDateTime.ofInstant(Instant.ofEpochMilli(o.getLong("firstEpisodePublishDt")), ZoneId.systemDefault()).toLocalDate())
                        .build())
                .collect(Collectors.toList());
    }

    private String toGenre(JSONArray array) {
        return StreamSupport.stream(array.spliterator(), false)
                .map(e -> (JSONObject)e)
                .map(g -> g.getString("title"))
                .collect(Collectors.joining("-"));
    }

    private ToonTitleDetail getDetail(ToonTitleDetail title) {
        Document doc = DocUtil.readFromUrl(title.getUrl());
        title.setAvgRating(Double.valueOf(doc.select(".score").text()));
        return title;
    }
}
