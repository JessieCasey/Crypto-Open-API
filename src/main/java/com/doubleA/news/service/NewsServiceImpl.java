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
        String uri = "https://www.alphavantage.co/query";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("function", "NEWS_SENTIMENT"));
        params.add(new BasicNameValuePair("tickers", "AAPL"));
        params.add(new BasicNameValuePair("topics", "technology"));
        params.add(new BasicNameValuePair("apikey", apiKey));

        new JSONObject(makeAPICall(uri, params)).getJSONArray("feed").forEach(x -> {
            newsRepository.insert(new Gson().fromJson(String.valueOf(x), News.class));
        });
    }

    @Override
    public Page<News> getPage(Query query, Pageable pageable) {
        return newsRepository.findAll(query, pageable);
    }
}
