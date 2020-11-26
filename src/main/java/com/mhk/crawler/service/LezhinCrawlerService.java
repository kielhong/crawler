package com.mhk.crawler.service;

import com.mhk.crawler.model.ToonEpisode;
import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitle;
import com.mhk.crawler.model.ToonTitleDetail;
import com.mhk.crawler.utl.JsonUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class LezhinCrawlerService implements CrawlerService {
    private static final String LZ_BASE_URL = "https://www.lezhin.com";
    private static final String LZ_WEEKLY_API_URL = LZ_BASE_URL + "/api/v2/inventory_groups/home_scheduled_k";
    private static final String LZ_FINISHED_API_URL = "https://www.lezhin.com/api/v2/comics";
    private static final String LZ_TITLE_BASE_URL = "http://webtoon.daum.net/webtoon/view/";
    private static final String LZ_EPISODE_API_URL = "http://webtoon.daum.net/data/pc/webtoon/view/";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<ToonTitle> listTitles() {
        List<ToonTitle> list = new ArrayList<>();

        return list;
    }

    @Override
    public List<ToonEpisode> listEpisodes(ToonTitle title) {
        return null;
    }

    public List<ToonTitleDetail> listTitleInfo() {
        List<ToonTitleDetail> list = new ArrayList<>();
//        list.addAll(listWeekTitles());            // 연재
        list.addAll(listCompletedTitles());         // 완결

        return list;
    }

    private List<ToonTitleDetail> listWeekTitles() {
        JSONObject jsonObject = new JSONObject(getJson());
        JSONArray inventories = jsonObject.getJSONObject("data").getJSONArray("inventoryList");

        List<ToonTitleDetail> titles = new ArrayList<>();
        for (int i = 2; i < inventories.length(); i++) {
            titles.addAll(listJsonArray(inventories.getJSONObject(i).getJSONArray("items")));
        }

        return titles;
    }

    private final static int offset = 36;
    private List<ToonTitleDetail> listCompletedTitles() {
        List<String> genres = Arrays.asList("_popular", "romance", "bl", "drama", "fantasy", "gag", "action", "school", "mystery", "day", "gl", "horror", "sports", "historical", "gore", "food", "martial", "tl", "gallery");
//        List<String> genres = Arrays.asList("gl");
        List<ToonTitleDetail> list = new ArrayList<>();
        for (String genre : genres) {
            for (int i = 0; i < 1000; i = i + offset) {
                List<ToonTitleDetail> tempList = listCompletedListFromApi(genre, i);
                if (!tempList.isEmpty()) {
                    list.addAll(tempList);
                } else {
                    break;
                }
            }
        }

        return list.stream()
                .distinct()
                .map(this::getDetail)
                .collect(Collectors.toList());
    }

    List<ToonTitleDetail> listJsonArray(JSONArray items) {
        return StreamSupport.stream(items.spliterator(), false)
                .map(e -> (JSONObject)e)
                .map(o -> ToonTitleDetail.builder()
                        .service(ToonService.LEZHIN)
                        .name(o.get("title").toString())
                        .key(o.get("alias").toString())
                        .genre(o.getJSONArray("genres").get(0).toString())
                        .keyword(StreamSupport.stream(o.getJSONArray("tags").spliterator(), false)
                                .map(e -> (String)e)
                                .collect(Collectors.joining("-")))
                        .date(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(o.get("createdAt").toString())), ZoneId.systemDefault()).toLocalDate())
                        .build()
                )
                .collect(Collectors.toList());
    }


    String getJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setAcceptLanguage(Locale.LanguageRange.parse("ko-KR"));
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String> response
                = restTemplate.exchange(LZ_WEEKLY_API_URL, HttpMethod.GET, entity, String.class);

        return response.getBody();

    }

    private List<ToonTitleDetail> listCompletedListFromApi(String genre, int offset) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setAcceptLanguage(Locale.LanguageRange.parse("ko-KR"));
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        String url = LZ_FINISHED_API_URL + "?offset=" + offset + "&country_code=kr&adult_kind=kid&filter=completed%2Cscheduled&genres=" + genre + "&limit=36&store=web&_=1599553847525";
        ResponseEntity<String> response
                = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());
        JSONArray data = jsonObject.getJSONArray("data");

        return StreamSupport.stream(data.spliterator(), false)
                .map(e -> (JSONObject)e)
                .map(o -> ToonTitleDetail.builder()
                        .service(ToonService.LEZHIN)
                        .name(o.get("title").toString())
                        .key(o.get("alias").toString())
                        .url(LZ_BASE_URL + "/ko/comic/" + o.get("alias").toString())
                        .genre(o.getJSONArray("genres").get(0).toString())
                        .build()
                )
                .collect(Collectors.toList());
    }

    private ToonTitleDetail getDetail(ToonTitleDetail title) {
        System.out.println(title.getUrl());
        try {
            String body = Jsoup.connect(title.getUrl()).get().html();
            //System.out.println(body);
            String patternString = "product: (\\{.+\\})";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                JSONObject jsonObject = new JSONObject(matcher.group(1));
//                System.out.println(jsonObject.getLong("publishedAt"));
//                System.out.println(LocalDateTime.ofInstant(Instant.ofEpochMilli(jsonObject.getLong("publishedAt")), ZoneId.systemDefault()).toLocalDate());
                title.setDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(jsonObject.getLong("publishedAt")), ZoneId.systemDefault()).toLocalDate());
                return title;
            } else {
//                System.out.println("not found");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return title;
    }
}
