package com.mhk.crawler;

import com.mhk.crawler.model.ToonEpisode;
import com.mhk.crawler.model.ToonService;
import com.mhk.crawler.model.ToonTitle;
import com.mhk.crawler.service.CrawlerFactory;
import com.mhk.crawler.service.CrawlerService;
import com.mhk.crawler.service.NaverCrawlerService;

import java.awt.*;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

/**
 * RUN COMMAND : ./gradlew bootRun -Pargs="naver|daum|kakao print|write"
 */
@SpringBootApplication
public class CrawlerApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(CrawlerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String serviceName = args.length > 0 ? args[0] : "naver";
		String output = args.length > 1 ? args[1] : "print";

		ToonService toonService = ToonService.valueOf(serviceName.toUpperCase());
		CrawlerService service = CrawlerFactory.from(toonService);

		System.out.println("OUTPUT : " + output);
		if ("write".equals(output)) {
					writeFile(toonService, service);
		} else {
			print(toonService, service);
		}
	}

	private void writeFile(ToonService toonService, CrawlerService service) throws Exception {
		String fileName = "webtoon_" + toonService.toString().toLowerCase() + "_episode_rating.csv";
		PrintWriter writer = new PrintWriter(fileName);

		int index = 1;
		List<ToonTitle> titles = service.listTitles();
		for (ToonTitle title : titles) {
			System.out.println("Title(" + (index++) + ") - " + title.getName());
			List<ToonEpisode> episodes = service.listEpisodes(title);
			episodes.forEach(e -> writer.println(e.toString()));
		}

		writer.close();
	}

	private void print(ToonService toonService, CrawlerService service) {
		String fileName = "webtoon_" + toonService.toString().toLowerCase() + "_episode_rating.csv";
		System.out.println("filename : " + fileName);

		int index = 1;
		List<ToonTitle> titles = service.listTitles();
		for (ToonTitle title : titles) {
			System.out.println("Title(" + (index++) + ") - " + title.getName());
			List<ToonEpisode> episodes = service.listEpisodes(title);
			episodes.forEach(e -> System.out.println(e.toString()));
		}
	}
}
