package com.mhk.crawler.utl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONObject;

public class JsonUtil {
    private static final String CHARSET = "UTF-8";

    public static JSONObject readFromUrl(String url) {
        return new JSONObject(getJsonString(url));
    }

    public static JSONObject readFromPostRequest(String url) {
        try {
            return new JSONObject(postRequest(url));
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private static String getJsonString(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName(CHARSET)));
            return readAll(rd);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static String postRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        byte[] postDataBytes = urlString.getBytes(CHARSET);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        try (InputStream is = conn.getInputStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName(CHARSET)));
            return readAll(rd);
        } catch (Exception e) {
            return "";
        }
    }
}
