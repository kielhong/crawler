package com.mhk.crawler.service;

import static org.assertj.core.api.BDDAssertions.then;

import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitleDetail;
import java.util.List;
import java.util.StringJoiner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MrblueCrawlerServiceTest {
    private MrblueCrawlerService service;

    @BeforeEach
    void setUp() {
        service = new MrblueCrawlerService();
    }

    @Test
    void listTitleDetails() {
        // when
        List<ToonTitleDetail> results = service.listTitleInfo();
        // then
        then(results).extracting("service")
                .containsOnly(ToonService.MRBLUE);
        System.out.println("미스터블루 non-19 작품 수 : " + results.size());

        results.forEach(e -> {
            StringJoiner sj = new StringJoiner(",");
            sj.add(e.getService().toString())
                    .add(e.getName())
                    .add(e.getGenre())
                    .add(e.getKey())
                    .add(e.getDate().toString())
                    .add(e.getAvgRating().toString());
            System.out.println(sj.toString());
        });
    }
}
