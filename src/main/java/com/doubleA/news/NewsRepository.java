package com.doubleA.news;

import com.doubleA.crypto.filter.repository.ResourceRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends ResourceRepository<News, String> {
}