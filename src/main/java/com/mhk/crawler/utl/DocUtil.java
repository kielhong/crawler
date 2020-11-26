package com.mhk.crawler.utl;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DocUtil {
    public static Document readFromUrl(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return new Document(url);
        }
    }
}
