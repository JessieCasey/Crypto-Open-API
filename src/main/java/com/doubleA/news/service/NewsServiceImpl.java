package com.doubleA.news.service;

import com.doubleA.news.News;
import com.doubleA.news.NewsRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.doubleA.crypto.service.CryptoServiceImpl.makeAPICall;

@Service
@Slf4j
public class NewsServiceImpl implements NewsService {

    private final String apiKey;
    private final NewsRepository newsRepository;

    public NewsServiceImpl(NewsRepository newsRepository,
                           @Value("${alpha.apikey}") String apiKey) {
        this.newsRepository = newsRepository;
        this.apiKey = apiKey;
    }

    @Override
    public void updateData() {
        String url = "https://www.alphavantage.co/query";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("function", "NEWS_SENTIMENT"));
        params.add(new BasicNameValuePair("tickers", "AAPL"));
        params.add(new BasicNameValuePair("topics", "technology"));
        params.add(new BasicNameValuePair("apikey", apiKey));

        new JSONObject(makeAPICall(url, params)).getJSONArray("feed").forEach(x -> {
            newsRepository.insert(new Gson().fromJson(String.valueOf(x), News.class));
        });
    }

    @Override
    public News getNewestNews() {
        return newsRepository.findAll().stream().min((o1, o2) -> {
            StringBuilder o1Time = new StringBuilder(o1.getTime()); // Format 2022-10-31T13:44:14
            StringBuilder o2Time = new StringBuilder(o2.getTime());

            o1Time.insert(4, "-");
            o2Time.insert(4, "-");

            o1Time.insert(7, "-");
            o2Time.insert(7, "-");

            o1Time.insert(13, ":");
            o2Time.insert(13, ":");

            o1Time.insert(16, ":");
            o2Time.insert(16, ":");

            return LocalDateTime.parse(o2Time).compareTo(LocalDateTime.parse(o1Time));
        }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Page<News> getPage(Query query, Pageable pageable) {
        return newsRepository.findAll(query, pageable);
    }
}
