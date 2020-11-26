package com.mhk.crawler.service;

import com.mhk.crawler.model.ToonEpisode;
import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitle;
import com.mhk.crawler.utl.JsonUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONObject;

public class KakaoCrawlerService implements CrawlerService {
    private static final String KKO_TITLE_LIST_API = "https://api2-page.kakao.com/api/v8/store/section_container/list?agent=web&category=10&subcategory=1000&day=0";
    @Override
    public List<ToonTitle> listTitles() {
        List<JSONObject> array = toTitleList(KKO_TITLE_LIST_API);

        return array.stream()
                .map(o -> ToonTitle.builder()
                        .service(ToonService.KAKAO)
                        .name(o.getString("title"))
                        .key(String.valueOf(o.getInt("series_id")))
                        .url("https://page.kakao.com/home?seriesId=" + o.getInt("series_id"))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ToonEpisode> listEpisodes(ToonTitle title) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        JSONObject jsonObject = JsonUtil.readFromPostRequest("https://api2-page.kakao.com/api/v5/store/singles?seriesid=55268637&page=0&direction=desc&page_size=20&without_hidden=true");
        JSONArray array = jsonObject.getJSONArray("singles");

        List<ToonEpisode> list = new ArrayList<>();
        for (int i = 0; i<array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            System.out.println(obj);
            ToonEpisode episode = ToonEpisode.builder()
                    .title(title)
                    .name(obj.getString("title"))
                    .date(LocalDate.parse(obj.getString("create_dt"), formatter))
                    .url("https://page.kakao.com/viewer?productId=" + obj.getInt("id"))
                    .build();
            list.add(episode);
        }
        return list;
    }

    private List<JSONObject> toTitleList(String url) {
        JSONObject jsonObject = JsonUtil.readFromUrl(url);
        JSONArray array = jsonObject
                .getJSONArray("section_containers")
                .getJSONObject(0)
                .getJSONArray("section_series")
                .getJSONObject(0)
                .getJSONArray("list");

        return StreamSupport.stream(array.spliterator(), false)
                .map(e -> (JSONObject)e)
                .collect(Collectors.toList());
    }
}
