package com.doubleA.news.service;

import com.doubleA.news.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

public interface NewsService {

    void updateData();

    Page<News> getPage(Query query, Pageable pageable);
}
