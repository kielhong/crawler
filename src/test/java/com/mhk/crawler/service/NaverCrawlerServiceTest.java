package com.mhk.crawler.service;

import static org.assertj.core.api.BDDAssertions.then;

import com.mhk.crawler.model.ToonEpisode;
import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitle;
import com.mhk.crawler.model.ToonTitleDetail;
import com.mhk.crawler.model.ToonTitleTestFixture;

import java.util.List;

import java.util.StringJoiner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class NaverCrawlerServiceTest {
    private NaverCrawlerService service;

    @BeforeEach
    void setUp() {
        service = new NaverCrawlerService();
    }

    @Test
    void listTitles() {
        // when
        List<ToonTitle> results = service.listTitles();
        // then
        then(results).extracting("service")
                .containsOnly(ToonService.NAVER);
        System.out.println("네이버 작품 수 : " + results.size());
        results.forEach(e -> System.out.println(e.getService() +"," + e.getName() + "," + e.getUrl() + "," + e.getKey()));
    }

    @Test
    void listTitleDetails() {
        // when
        List<ToonTitleDetail> results = service.listTitleInfo();
        // then
        then(results).extracting("service")
                .containsOnly(ToonService.NAVER);
        System.out.println("네이버 작품 수 : " + results.size());

        results.forEach(e -> {
            StringJoiner sj = new StringJoiner(",");
            sj.add(e.getService().toString());
            sj.add(e.getName());
            sj.add(e.getGenre());
            sj.add(e.getKey());
            sj.add(e.getDate().toString());
            sj.add(e.getAvgRating().toString());

            System.out.println(sj.toString());
        });
    }

    @Test
    void listEpisodes() {
        // given
        ToonTitle title = ToonTitleTestFixture.life();
        // when
        List<ToonEpisode> results = service.listEpisodes(title);
        // then
        then(results).extracting("title.service")
                .containsOnly(ToonService.NAVER);
        results.stream()
                .forEach(System.out::println);
    }
}