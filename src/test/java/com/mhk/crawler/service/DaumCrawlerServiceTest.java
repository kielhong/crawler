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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DaumCrawlerServiceTest {
    private DaumCrawlerService service;

    @BeforeEach
    void setUp() {
        service = new DaumCrawlerService();
    }

    @Test
    void listTitles() {
        // when
        List<ToonTitle> results = service.listTitles();
        // then
        then(results).extracting("service")
                .containsOnly(ToonService.DAUM);
        System.out.println("다음 작품 수 : " + results.size());
        results.forEach(e -> System.out.println(e.getService() +"," + e.getName() + "," + e.getUrl()));
    }

    @Test
    void listEpisodes() {
        // given
        ToonTitle title = ToonTitleTestFixture.itaewon();
        // when
        List<ToonEpisode> results = service.listEpisodes(title);
        // then
        then(results).extracting("title.service")
                .containsOnly(ToonService.DAUM);
        results.forEach(System.out::println);
    }

    @Test
    @DisplayName("다음 웹툰 작품별 상세 정보")
    void listTitleDetails() {
        // when
        List<ToonTitleDetail> results = service.listTitleInfo();
        // then
        then(results).extracting("service")
                .containsOnly(ToonService.DAUM);
        System.out.println("다음 작품 수 : " + results.size());

        results.forEach(e -> {
            StringJoiner sj = new StringJoiner(",");
            sj.add(e.getService().toString());
            sj.add(e.getName());
            sj.add(e.getGenre());
            sj.add(e.getKeyword());
            sj.add(e.getKey());
            sj.add(e.getDate().toString());
            sj.add(e.getTotalVote().toString());

            System.out.println(sj.toString());
        });
    }
}