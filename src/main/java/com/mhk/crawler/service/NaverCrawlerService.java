package com.mhk.crawler.service;

import com.mhk.crawler.model.ToonEpisode;
import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitle;
import com.mhk.crawler.model.ToonTitleDetail;
import com.mhk.crawler.utl.DocUtil;
import com.mhk.crawler.utl.JsonUtil;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NaverCrawlerService implements CrawlerService {
    private static final String NT_BASE_URL = "https://comic.naver.com";
    private static final String NT_WEEKLY_URL = NT_BASE_URL + "/webtoon/weekday.nhn";
    private static final String NT_FINISH_URL = NT_BASE_URL + "/webtoon/finish.nhn";
    private static final String NT_TITLE_DETAIL_URL = "https://comic.naver.com/webtoon/list.nhn";
    private static final String LIKE_API_URL = "https://comic.like.naver.com/likeIt/v1/likeItServiceContentList.jsonp";

    @Override
    public List<ToonTitle> listTitles()  {
        return Stream.of(NT_WEEKLY_URL, NT_FINISH_URL)
                .map(this::listTitles)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<ToonEpisode> listEpisodes(ToonTitle title) {
        List<ToonEpisode> episodes = new ArrayList<>();

        int maxNo = getMaxNo(title);
        int page = (maxNo / 10) + 1;

        for (int i = 1; i <= page; i++) {
            episodes.addAll(listEpisodes(title, i));
        }

        return episodes;
    }

    public List<ToonTitleDetail> listTitleInfo() {
        List<ToonTitle> titles = Stream.of(NT_WEEKLY_URL, NT_FINISH_URL)
                .map(this::listTitles)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return titles.stream()
                .map(this::getTitleInfo)
                .collect(Collectors.toList());
    }

    private List<ToonTitle> listTitles(String baseUrl) {
        Document doc = DocUtil.readFromUrl(baseUrl);

        Elements elements = doc.select(".thumb a");
        return elements.stream()
                .map(e -> ToonTitle.builder()
                        .service(ToonService.NAVER)
                        .name(e.children().attr("title"))
                        .url(NT_BASE_URL + e.attr("href"))
                        .key(getTitleId(e.attr("href")))
                        .build())
                .collect(Collectors.toList());
    }

    private ToonTitleDetail getTitleInfo(ToonTitle title) {
        Document doc = DocUtil.readFromUrl(NT_TITLE_DETAIL_URL + "?titleId=" + title.getKey());

        System.out.println(title.getName());
        System.out.println(NT_TITLE_DETAIL_URL + "?titleId=" + title.getKey());
        //System.out.println(doc.select(".genre").text().replace(",", "-"));

        List<ToonEpisode> episodes = listEpisodes(title);
//        System.out.println(episodes.stream().map(ToonEpisode::getDate).forEach(e->System.out.println(e)));
//        episodes.stream().map(ToonEpisode::getDate).forEach(e->System.out.println(e));
//        System.out.println(episodes.get(episodes.size() - 1).getDate());
//            .min(Comparator.comparing(LocalDate::toEpochDay)));
//        System.out.println("votes : " + episodes.stream().mapToInt(ToonEpisode::getVote).sum());
//        System.out.println("votes : " + episodes.stream().mapToDouble(ToonEpisode::getRating).average().getAsDouble());
        return ToonTitleDetail.builder()
                .service(title.getService())
                .name(title.getName())
                .key(title.getKey())
                .genre(doc.select(".genre").text().replace(", ", "-"))
                .date(episodes.get(episodes.size() - 1).getDate())
                .avgRating(episodes.stream().mapToDouble(ToonEpisode::getRating).average().getAsDouble())
//                .totalVote(episodes.stream().mapToInt(ToonEpisode::getVote).sum())
                .build();
    }

    private List<ToonEpisode> listEpisodes(ToonTitle title, int page) {
        Document doc = DocUtil.readFromUrl(title.getUrl() + "&page=" + page);

        DayOfWeek day = getDayOfWeek(title);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        Elements trs = doc.select("table.viewList tr:gt(0)").not("tr.band_banner");
        return trs.stream()
                .map(row -> row.select("td"))
                .map(tds -> ToonEpisode.builder()
                                .title(title)
                                .name(tds.get(1).text())
                                .date(LocalDate.parse(tds.get(3).text(), formatter))
                                .day(day)
                                .rating(Float.valueOf(tds.get(2).select("strong").text()))
//                                .vote(getVote(title.getKey(), getEpisodeId(tds.get(1).select("a").attr("href"))))
                                .build())
                .collect(Collectors.toList());
    }

    private int getMaxNo(ToonTitle title) {
        Document doc = DocUtil.readFromUrl(title.getUrl());

        String lastUrl = "";
        Elements trs = doc.select("table.viewList tr");
        for (Element tr : trs){
            Elements tds = tr.select("td.title");
            if (tds.size() > 0){
                Element td = tds.get(0);
                lastUrl = td.select("a").first().attr("href");
                break;
            }
        }

        Pattern pattern = Pattern.compile("no=([0-9]+)");
        Matcher matcher = pattern.matcher(lastUrl);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group(1));
        }

        return 0;
    }

    private String getTitleId(String titleUrl) {
        Pattern pattern = Pattern.compile("titleId=([0-9]+)");
        Matcher matcher = pattern.matcher(titleUrl);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "0";
        }
    }

    private String getEpisodeId(String episodeUrl) {
        Pattern pattern = Pattern.compile("no=([0-9]+)");
        Matcher matcher = pattern.matcher(episodeUrl);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "0";
        }
    }

    private Integer getVote(String titleId, String episodeId) {
        JSONObject jsonObject;
        try {
            jsonObject = JsonUtil.readFromUrl(LIKE_API_URL + "?params=COMIC[" + titleId + "_" + episodeId + "]");
            JSONArray array = jsonObject.getJSONObject("result").getJSONArray("contents");
            if (array.length() > 0) {
                return array.getJSONObject(0).getInt("likeItCount");
            }
        } catch (Exception e) {
            jsonObject = JsonUtil.readFromUrl(LIKE_API_URL + "?params=COMIC[" + titleId + "_" + episodeId + "]");
            JSONArray array = jsonObject.getJSONObject("result").getJSONArray("contents");
            if (array.length() > 0) {
                return array.getJSONObject(0).getInt("likeItCount");
            }
        }

        return 0;
    }

    private DayOfWeek getDayOfWeek(ToonTitle title) {
        Pattern pattern = Pattern.compile("weekday=([a-z]+)");
        Matcher matcher = pattern.matcher(title.getUrl());
        if (matcher.find()) {
            String day = matcher.group(1);
            switch (day) {
                case "mon" : return DayOfWeek.MONDAY;
                case "tue" : return DayOfWeek.TUESDAY;
                case "wed" : return DayOfWeek.WEDNESDAY;
                case "thu" : return DayOfWeek.THURSDAY;
                case "fri" : return DayOfWeek.FRIDAY;
                case "sat" : return DayOfWeek.SATURDAY;
                case "sun" : return DayOfWeek.SUNDAY;
            }
        }

        return DayOfWeek.MONDAY;
    }
}