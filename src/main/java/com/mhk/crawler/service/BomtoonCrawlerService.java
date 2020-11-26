package com.mhk.crawler.service;

import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitleDetail;
import com.mhk.crawler.utl.DocUtil;
import com.mhk.crawler.utl.JsonUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

public class BomtoonCrawlerService {
    private final static String BASE_URL = "https://www.bomtoon.com";
    private final static String WEEKLY_TITLE_URL = BASE_URL + "/main/weekly";
    private final static String COMPLETE_TITLE_URL = BASE_URL + "/main/complete";
    private final static String WAITFREE_TITLE_URL = "https://api-bufftoon.plaync.com/v1/contents/webtoon?categoryIdx=1061&displaySeriesType=3&offset=0&limit=300&version=2";
    private final static String TITLE_DETAIL_URL = BASE_URL + "/series";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public List<ToonTitleDetail> listTitleInfo() {
        List<ToonTitleDetail> list = new ArrayList<>();
        list.addAll(listWeeklyTitles());
        list.addAll(listCompletedTitles());
        list.addAll(listShortTitles());

        return list.stream()
                .distinct()
                .map(this::getTitleDetail)
                .collect(Collectors.toList());
    }

    private List<ToonTitleDetail> listWeeklyTitles() {
        JSONObject jsonObject = JsonUtil.readFromPostRequest("https://www.bomtoon.com/weekly/list/all");
        Document doc = Jsoup.parse(jsonObject.getString("html").toString());

        return doc.select("li").stream()
                .map(e -> ToonTitleDetail.builder()
                        .service(ToonService.BOMTOON)
                        .key(e.select("a").attr("href").split("/")[3])
                        .url(BASE_URL + e.select("a").attr("href"))
                        .build())
                .collect(Collectors.toList());
    }

    private List<ToonTitleDetail> listCompletedTitles() {
        List<ToonTitleDetail> list = new ArrayList<>();
        for (int i = 0; i < 1000; i = i + 20) {
            String html = new JSONObject(getJson("https://www.bomtoon.com/complete/list/1", i)).getString("html");
            if (StringUtils.isEmpty(html)) {
                break;
            }
            Document doc = Jsoup.parse(html);

//            System.out.println(doc.select("li").stream().findFirst().get());
            list.addAll(doc.select("li").stream()
                    .map(e -> ToonTitleDetail.builder()
                            .service(ToonService.BOMTOON)
                            .key(e.select("a").attr("href").split("/")[3])
                            .url(BASE_URL + e.select("a").attr("href"))
                            .build())
                    .collect(Collectors.toList()));
        }

        return list;
    }

    private List<ToonTitleDetail> listShortTitles() {
        List<ToonTitleDetail> list = new ArrayList<>();
        for (int i = 0; i < 1000; i = i + 20) {
            String html = new JSONObject(getJson("https://www.bomtoon.com/shorttoon/list/1", i)).getString("html");
            if (StringUtils.isEmpty(html)) {
                break;
            }
            Document doc = Jsoup.parse(html);

//            System.out.println(doc.select("li").stream().findFirst().get());
            list.addAll(doc.select("li").stream()
                    .map(e -> ToonTitleDetail.builder()
                            .service(ToonService.BOMTOON)
                            .key(e.select("a").attr("href").split("/")[3])
                            .url(BASE_URL + e.select("a").attr("href"))
                            .build())
                    .collect(Collectors.toList()));
        }

        return list;
    }

    private ToonTitleDetail getTitleDetail(ToonTitleDetail title) {
        Document doc = DocUtil.readFromUrl(title.getUrl());
//        System.out.println(title.getUrl());
        String tags = doc.select("div.tags > a").stream().map(e -> e.text()).collect(Collectors.joining("-"));
        String genre = tags.split("-")[0];
        title.setName(doc.select("p#bt-comic-name").text());
        title.setGenre(genre);
        title.setKeyword(tags);
        title.setDate(LocalDate.parse(doc.select("p.c-a4.mt-5").first().text(), formatter));
        return title;
    }

    private String getJson(String url, int offset) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("offset", String.valueOf(offset));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request , String.class );

//        System.out.println(response.getBody());

        return response.getBody();
    }
}
