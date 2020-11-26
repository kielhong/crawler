package com.mhk.crawler.service;

import static org.assertj.core.api.BDDAssertions.then;

import com.mhk.crawler.model.ToonEpisode;
import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitle;
import com.mhk.crawler.model.ToonTitleTestFixture;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KakaoCrawlerServiceTest {
    private KakaoCrawlerService service;

    @BeforeEach
    void setUp() {
        service = new KakaoCrawlerService();
    }

    @Test
    void listTitles() {
        // when
        List<ToonTitle> results = service.listTitles();
        // then
        then(results).extracting("service")
                .containsOnly(ToonService.KAKAO);
        System.out.println("카카오 작품 수 : " + results.size());
        results.forEach(e -> System.out.println(e.getService() +"," + e.getName().replace(",", "") + "," + e.getUrl()));
    }

    @Test
    void listEpisodes() {
        // given
        ToonTitle title = ToonTitleTestFixture.romeo();
        // when
        List<ToonEpisode> results = service.listEpisodes(title);
        // then
        then(results).extracting("title.service")
                .containsOnly(ToonService.KAKAO);
        results.forEach(System.out::println);
    }
}