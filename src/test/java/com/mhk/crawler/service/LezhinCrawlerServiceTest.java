package com.mhk.crawler.service;

import static org.assertj.core.api.BDDAssertions.then;

import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitleDetail;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.StringJoiner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LezhinCrawlerServiceTest {
    private LezhinCrawlerService service;

    @BeforeEach
    void setUp() {
        service = new LezhinCrawlerService();
    }

    @Test
    void listTitleDetails() {
        // when
        List<ToonTitleDetail> results = service.listTitleInfo();
        // then
        then(results).extracting("service")
                .containsOnly(ToonService.LEZHIN);
        System.out.println("레진 non-19 작품 수 : " + results.size());

        results.forEach(e -> {
            StringJoiner sj = new StringJoiner(",");
            sj.add(e.getService().toString());
            sj.add(e.getName());
            sj.add(e.getGenre());
//            sj.add(e.getKeyword());
            sj.add(e.getKey());
            sj.add(e.getDate().toString());
            System.out.println(sj.toString());
        });
    }

//    @Test
//    void test() {
//        System.out.println(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(1435755128000L)), ZoneId.systemDefault()).toLocalDate());
//    }
}
