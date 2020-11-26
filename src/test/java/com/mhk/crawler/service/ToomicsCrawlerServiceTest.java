package com.mhk.crawler.service;

import static org.assertj.core.api.BDDAssertions.then;

import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitleDetail;
import java.util.List;
import java.util.StringJoiner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ToomicsCrawlerServiceTest {
    private ToomicsCrawlerService service;

    @BeforeEach
    void setUp() {
        service = new ToomicsCrawlerService();
    }

    @Test
    void listTitleDetails() {
        // when
        List<ToonTitleDetail> results = service.listTitleInfo();
        // then
        then(results).extracting("service")
                .containsOnly(ToonService.TOOMICS);
        System.out.println("투믹스 non-19 작품 수 : " + results.size());

        results.forEach(e -> {
            StringJoiner sj = new StringJoiner(",");
            sj.add(e.getService().toString())
                    .add(e.getName())
                    .add(e.getGenre())
                    .add(e.getKeyword())
                    .add(e.getKey())
                    .add(e.getDate().toString());
            System.out.println(sj.toString());
        });
    }
}
