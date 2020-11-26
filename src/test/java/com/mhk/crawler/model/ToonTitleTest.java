package com.mhk.crawler.model;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

class ToonTitleTest {
    @Test
    void construct() {
        // when
        ToonTitle toonTitle = ToonTitle.builder()
                .name("name")
                .service(ToonService.NAVER)
                .url("http://toon/url")
                .build();
        // then
        then(toonTitle)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("service", ToonService.NAVER)
                .hasFieldOrPropertyWithValue("url", "http://toon/url");
    }
}