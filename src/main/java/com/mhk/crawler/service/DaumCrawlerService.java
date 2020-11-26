package com.mhk.crawler.service;

import com.mhk.crawler.model.ToonEpisode;
import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitle;
import com.mhk.crawler.model.ToonTitleDetail;
import com.mhk.crawler.utl.JsonUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.json.JSONArray;
import org.json.JSONObject;

public class DaumCrawlerService implements CrawlerService {
    private static final String DM_BASE_URL = "http://webtoon.daum.net";
    private static final String DM_WEEKLY_API_URL = "http://webtoon.daum.net/data/pc/webtoon/list_serialized/";
    private static final String DM_FINISHED_API_URL = "http://webtoon.daum.net/data/pc/webtoon/list_finished/?genre_id=";
    private static final String DM_TITLE_BASE_URL = "http://webtoon.daum.net/webtoon/view/";
    private static final String DM_EPISODE_API_URL = "http://webtoon.daum.net/data/pc/webtoon/view/";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public List<ToonTitle> listTitles() {
        List<ToonTitle> list = new ArrayList<>();
        list.addAll(listWeekTitles());
        list.addAll(listFinishedTitles());

        return list;
    }

    @Override
    public List<ToonEpisode> listEpisodes(ToonTitle title) {
        String[] tokens = title.getUrl().split("/");
        String nickname = tokens[tokens.length - 1];

        JSONObject jsonObject = JsonUtil.readFromUrl(DM_EPISODE_API_URL + nickname);
        if (jsonObject.getJSONObject("result").getInt("status") != 200) {
            return Collections.emptyList();
        }
        JSONObject root = jsonObject.getJSONObject("data").getJSONObject("webtoon");

        DayOfWeek day = getDayOfWeek(root);

        JSONArray array = root.getJSONArray("webtoonEpisodes");

        return StreamSupport.stream(array.spliterator(), false)
                .map(e -> (JSONObject)e)
                .map(obj -> ToonEpisode.builder()
                        .title(title)
                        .name(obj.getString("title"))
                        .day(day)
                        .date(LocalDate.parse(obj.getString("dateCreated"), formatter))
                        .vote(obj.getJSONObject("voteTarget").getInt("voteTotalScore"))
                        .free(obj.getString("serviceType").equals("free"))
                        .build())
                .collect(Collectors.toList());
    }

    public List<ToonTitleDetail> listTitleInfo() {
        return this.listTitles().stream()
                .map(this::getTitleInfo)
                .filter(o -> o != null)
                .collect(Collectors.toList());
    }

    private ToonTitleDetail getTitleInfo(ToonTitle title) {
        String[] tokens = title.getUrl().split("/");
        String nickname = tokens[tokens.length - 1];

        System.out.println(title.getName());
//        System.out.println(DM_EPISODE_API_URL + nickname);
        JSONObject jsonObject = JsonUtil.readFromUrl(DM_EPISODE_API_URL + nickname);

        if (jsonObject.getJSONObject("result").getInt("status") != 200) {
            return null;
        }

        JSONObject webtoon = jsonObject.getJSONObject("data").getJSONObject("webtoon");
        JSONObject cartoon = webtoon.getJSONObject("cartoon");
//        System.out.println(listEpisodes(title).stream().mapToInt(ToonEpisode::getVote).sum());
//        System.out.println(cartoon.getJSONArray("genres").getJSONObject(0).get("name").toString());
//        JSONArray categories = cartoon.getJSONArray("categories");
//        System.out.println(LocalDate.parse(webtoon.get("startDate").toString(), formatter));
//        System.out.println(StreamSupport.stream(categories.spliterator(), false)
//                .map(e -> (JSONObject)e)
//                .map(o -> o.get("name").toString())
//                .collect(Collectors.joining("-")));
        return ToonTitleDetail.builder()
                .service(title.getService())
                .name(title.getName())
                .key(nickname)
                .genre(cartoon.getJSONArray("genres").getJSONObject(0).get("name").toString())
                .keyword(StreamSupport.stream(cartoon.getJSONArray("categories").spliterator(), false)
                        .map(e -> (JSONObject)e)
                        .map(o -> o.get("name").toString())
                        .collect(Collectors.joining("-")))
                .date(LocalDate.parse(webtoon.get("startDate").toString(), formatter))
//                .avgRating(episodes.stream().mapToDouble(ToonEpisode::getRating).average().getAsDouble())
                .totalVote(listEpisodes(title).stream().mapToInt(ToonEpisode::getVote).sum())
                .build();
    }

    private List<ToonTitle> listWeekTitles() {
        List<String> days = Arrays.asList("mon", "tue", "wed", "thu", "fri", "sat", "sun");

        return days.stream()
                .map(day -> DM_WEEKLY_API_URL + day)
                .map(JsonUtil::readFromUrl)
                .map(DaumCrawlerService::toDataList)
                .flatMap(List::stream)
                .map(o -> ToonTitle.builder()
                        .url(getTitleUrl(o))
                        .name(o.get("title").toString())
                        .service(ToonService.DAUM)
                        .build())
                .collect(Collectors.toList());
    }

    private List<ToonTitle> listFinishedTitles() {
        JSONObject jsonObject = JsonUtil.readFromUrl(DM_FINISHED_API_URL);

//        System.out.println(jsonObject.toString());
        return toDataList(jsonObject).stream()
                .map(o -> ToonTitle.builder().
                        url(getTitleUrl(o))
                        .name(o.get("title").toString())
                        .service(ToonService.DAUM)
                        .build())
                .collect(Collectors.toList());
    }

    private String getTitleUrl(JSONObject jsonObject) {
        return DM_TITLE_BASE_URL + jsonObject.get("nickname");
    }

    private static List<JSONObject> toDataList(JSONObject jsonObject) {
        JSONArray data = jsonObject.getJSONArray("data");

        return StreamSupport.stream(data.spliterator(), false)
                .map(e -> (JSONObject)e)
                .collect(Collectors.toList());
    }

    private DayOfWeek getDayOfWeek(JSONObject jsonObject) {
        JSONArray week = jsonObject.getJSONArray("webtoonWeeks");
        if (week.length() != 0) {
            switch (week.getJSONObject(0).getString("weekDay")) {
                case "mon" : return DayOfWeek.MONDAY;
                case "tue" : return DayOfWeek.TUESDAY;
                case "wed" : return DayOfWeek.WEDNESDAY;
                case "thu" : return DayOfWeek.THURSDAY;
                case "fri" : return DayOfWeek.FRIDAY;
                case "sat" : return DayOfWeek.SATURDAY;
                case "sun" : return DayOfWeek.SUNDAY;
            }
        }

        return null;
    }

}