package com.mhk.crawler.model;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

class ToonEpisodeTest {
    @Test
    void construct() {
        // given
        ToonTitle toonTitle = ToonTitleTestFixture.sound();
        // when
        ToonEpisode episode = ToonEpisode.builder()
                .title(toonTitle)
                .name("episode")
                .url("http://episode/url")
                .vote(455)
                .rating(5.99f)
                .build();
        // then
        then(episode)
                .hasFieldOrPropertyWithValue("title", toonTitle)
                .hasFieldOrPropertyWithValue("name", "episode")
                .hasFieldOrPropertyWithValue("url", "http://episode/url")
                .hasFieldOrPropertyWithValue("vote", 455L)
                .hasFieldOrPropertyWithValue("rating", 5.99f);
    }
}