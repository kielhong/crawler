package com.mhk.crawler.service;

import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitle;
import com.mhk.crawler.model.ToonTitleDetail;
import com.mhk.crawler.utl.DocUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.text.html.parser.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ToomicsCrawlerService {
    private final static String BASE_URL = "https://www.toomics.com";
    private final static String WEEKLY_TITLE_URL = BASE_URL + "/webtoon/weekly/dow/";
    private final static String TITLE_DETAIL_URL = BASE_URL + "/webtoon/episode/toon/";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public List<ToonTitleDetail> listTitleInfo() {
        List<ToonTitleDetail> titles = new ArrayList<>();
        titles.addAll(listWeeklyTitles());
        titles.addAll(listOtherTitles());
        titles.addAll(listFinishedTitles());
        titles.addAll(listMonthlyTitles());

        return titles.stream()
                .distinct()
                .sorted(Comparator.comparing(ToonTitleDetail::getKey))
                .map(this::getTitleDetail)
                .collect(Collectors.toList());
    }

    private ToonTitleDetail getTitleDetail(ToonTitleDetail title) {
        Document doc = DocUtil.readFromUrl(TITLE_DETAIL_URL + title.getKey());

        String keyword = doc.select("a.tag").stream()
                .map(t -> t.text().replace("#", ""))
                .collect(Collectors.joining("-"));
        LocalDate date = LocalDate.parse(doc.select("span.ep__date").first().text(), formatter);

        title.setKeyword(keyword);
        title.setDate(date);

        return title;
    }

    private List<ToonTitleDetail> listWeeklyTitles() {
        List<String> list = Arrays.asList("1", "2", "3", "4", "5", "6", "7");

        return list.stream()
            .map(i -> listTitleOfSection(WEEKLY_TITLE_URL + i))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<ToonTitleDetail> listOtherTitles() {
        return listTitleOfSection(BASE_URL + "/webtoon/toon_list/display/G2");
    }

    private List<ToonTitleDetail> listFinishedTitles() {
        return listTitleOfSection(BASE_URL + "/webtoon/finish/all");
    }

    private List<ToonTitleDetail> listMonthlyTitles() {
         return DocUtil.readFromUrl(BASE_URL + "/webtoon/monthly")
                 .select("li.grid__li")
                 .stream()
                 .filter(t -> t.select("a").hasText())
//                 .forEach(t -> System.out.println(t.select("a").attr("href") + "," + t.select("a").attr("href").split("/").length));
                .map(t -> ToonTitleDetail.builder()
                        .service(ToonService.TOOMICS)
                        .key(t.select("a").attr("href").split("/")[6])
                        .name(t.select(".toon-monthly__title").text())
                        .genre(t.select(".toon-monthly__link").text())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ToonTitleDetail> listTitleOfSection(String url) {
        Document doc = DocUtil.readFromUrl(url);

        return DocUtil.readFromUrl(url)
                .select("li.grid__li")
                .stream()
                .map(t -> ToonTitleDetail.builder()
                        .service(ToonService.TOOMICS)
                        .key(t.select("a").attr("href").toString().split("/")[6])
                        .name(t.select(".toon-dcard__title").text())
                        .genre(t.select(".toon-dcard__link").text())
                        .build())
                .collect(Collectors.toList());
    }



}
