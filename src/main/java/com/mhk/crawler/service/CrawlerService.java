package com.mhk.crawler.service;

import com.mhk.crawler.model.ToonEpisode;
import com.mhk.crawler.model.ToonTitle;

import java.util.List;

public interface CrawlerService {
    List<ToonTitle> listTitles();

    List<ToonEpisode> listEpisodes(ToonTitle title);
}
