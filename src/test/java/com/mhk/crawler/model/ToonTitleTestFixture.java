package com.mhk.crawler.model;

import java.util.Arrays;
import java.util.List;

public class ToonTitleTestFixture {
    private static ToonTitle SOUND = ToonTitle.builder()
            .service(ToonService.NAVER)
            .name("마음의소리")
            .url("https://comic.naver.com/webtoon/list.nhn?titleId=20853&weekday=tue")
            .key("20853")
            .build();
    private static ToonTitle YUMI = ToonTitle.builder()
            .service(ToonService.NAVER)
            .name("유미의 세포들")
            .url("https://comic.naver.com/webtoon/list.nhn?titleId=651673&weekday=sat")
            .key("651673")
            .build();
    private static ToonTitle LIFE = ToonTitle.builder()
            .service(ToonService.NAVER)
            .name("인생존망")
            .url("https://comic.naver.com/webtoon/list.nhn?titleId=733766&weekday=mon")
            .key("733766")
            .build();
    private static ToonTitle SEOCHEON = ToonTitle.builder()
            .service(ToonService.DAUM)
            .name("서천화원")
            .url("http://webtoon.daum.net/webtoon/view/seocheon")
            .key("seocheon")
            .build();
    private static ToonTitle NONSTOP = ToonTitle.builder()
            .service(ToonService.DAUM)
            .name("이대 멈출 수 없다")
            .url("http://webtoon.daum.net/webtoon/view/notstopping")
            .key("notstopping")
            .build();
    private static ToonTitle ITAEWON = ToonTitle.builder()
            .service(ToonService.DAUM)
            .name("이태원 클라쓰")
            .url("http://webtoon.daum.net/webtoon/view/ItaewonClass")
            .key("ItaewonClass")
            .build();
    private static ToonTitle ROMEO = ToonTitle.builder()
            .service(ToonService.KAKAO)
            .name("나오세요, 로미")
            .url("https://page.kakao.com/home?seriesId=55268637")
            .key("55268637")
            .build();


    public static ToonTitle sound() {
        return SOUND;
    }

    public static ToonTitle yumi() {
        return YUMI;
    }

    public static ToonTitle life() {
        return LIFE;
    }

    public static ToonTitle west() { return SEOCHEON; }

    public static ToonTitle nonstop() { return NONSTOP; }

    public static ToonTitle itaewon() { return ITAEWON; }

    public static ToonTitle romeo() { return ROMEO; }

    public static List<ToonTitle> navers() {
        return Arrays.asList(SOUND, YUMI, LIFE);
    }

    public static List<ToonTitle> daums() {
        return Arrays.asList(SEOCHEON, NONSTOP, ITAEWON);
    }
}
