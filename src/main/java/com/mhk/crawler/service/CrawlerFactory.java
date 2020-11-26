package com.mhk.crawler.service;

import com.mhk.crawler.model.ToonService;

public class CrawlerFactory {
    public static CrawlerService from(ToonService service) {
        switch (service) {
            case KAKAO : return new KakaoCrawlerService();
            case DAUM  : return new DaumCrawlerService();
            case NAVER :
            default:
                return new NaverCrawlerService();
        }
    }
}
