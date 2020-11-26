package com.mhk.crawler.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;

public class KeywordService {

    public void check() throws Exception {
        List<String> keywords = listKeyword();

        keywords.stream()
                .map(k -> {
                    System.out.print(k + ",");
                    try {
                        return "https://api.manhwakyung.com/titles/search?q=" + URLEncoder.encode(k, "UTF-8");
                    } catch (Exception e) {
                        return "";
                    }
                })
                .map(s -> readJsonFromUrl(s))
                .forEach(a -> System.out.println(a.length() > 0 ? "Y" : "N"));
    }


    private static JSONArray readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()){
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            return new JSONArray(readText(rd));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONArray();
    }

    private static String readText(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private List<String> listKeyword() {
        return Arrays.asList("윈터게임",
                "winter game",
                "윈터",
                "winter",
                "감자",
                "개구리",
                "마카롱",
                "개구리공주",
                "윌슨",
                "게임",
                "동네",
                "text",
                "윌슨가의 비밀",
                "윌슨가",
                "로맨스",
                "유진",
                "키크니",
                "개구리 공주",
                "전학생",
                "씨씨",
                "동네한바퀴",
                "원터게임",
                "윈터 게임",
                "호랑이",
                "고제형",
                "",
                "귀멸의 칼날",
                "win",
                "wintergame",
                "원터",
                "독립",
                "귀멸의칼날",
                "연애",
                "가족",
                "별일",
                "동네 한 바퀴",
                "윈",
                "개",
                "지박소년 하나코군",
                "이태원 클라쓰",
                "안녕안녕해",
                "쎄쎄쎄",
                "공포",
                "동네 한바퀴",
                "나의",
                "비밀",
                "독립시험",
                "채대리",
                "w",
                "할아버지",
                "나의 연애",
                "나의연애",
                "나의",
                "연애",
                "윌슨",
                "윈터게임",
                "winter",
                "윈터",
                "개구리",
                "winter game",
                "윌슨가의 비밀",
                "윌슨가",
                "d",
                "개구리공주",
                "day",
                "감자",
                "d-day",
                "오늘",
                "D",
                "로맨스",
                "나의 연애 D-day",
                "유진",
                "d day",
                "win",
                "게임",
                "비밀",
                "디데이",
                "D-day",
                "개구리 공주",
                "윌슨가의",
                "",
                "마카롱",
                "나의 연애",
                "전학생",
                "씨씨",
                "나의연애 D-DAY",
                "만화경",
                "키크니",
                "동네",
                "윌",
                "쎄쎄쎄",
                "원터게임",
                "나의연애 D-day",
                "윌슨가의비밀",
                "안녕",
                "dday",
                "나의",
                "사랑",
                "이",
                "스라소니",
                "나의 연애",
                "나의연애",
                "나의",
                "연애",
                "winter",
                "킬러",
                "오버사이즈러브",
                "winter game",
                "오늘",
                "오버",
                "윈터게임",
                "중간계 사우나",
                "나의 연애 D-day",
                "개구리",
                "로맨스",
                "마이킬러",
                "중간계",
                "감자",
                "개구리공주",
                "윈터",
                "day",
                "d",
                "오버사이즈",
                "d-day",
                "윌슨",
                "D",
                "나의 연애",
                "my killer",
                "사우나",
                "d day",
                "씨씨",
                "전학생",
                "my",
                "나의연애 D-DAY",
                "D-day",
                "종이비행기를 날리면",
                "마카롱",
                "",
                "러브",
                "윌슨가의 비밀",
                "만화경",
                "리버스 러브",
                "디데이",
                "win",
                "쎄쎄쎄",
                "개구리 공주",
                "나의연애 D-day",
                "BL",
                "나의 연애 D-DAY",
                "유진",
                "나의 연애",
                "연애",
                "나의연애",
                "나의",
                "감자",
                "winter",
                "개구리",
                "중간계 사우나",
                "오늘",
                "로맨스",
                "개구리공주",
                "중간계",
                "winter game",
                "사우나",
                "오버",
                "오버사이즈러브",
                "윈터게임",
                "day",
                "종이비행기를 날리면",
                "d",
                "개구리 공주",
                "씨씨",
                "매화",
                "윌슨",
                "윈터",
                "D",
                "win",
                "고요빛",
                "동물",
                "d day",
                "킬러",
                "숫자",
                "오버사이즈",
                "",
                "중간",
                "디데이",
                "종이",
                "유진",
                "윌슨가의 비밀",
                "사랑",
                "전학생",
                "d-day",
                "종이비행기",
                "w",
                "bl",
                "스라소니",
                "마카롱",
                "나의 연애 D-day",
                "리버스 러브",
                "리버스");
    }
}
