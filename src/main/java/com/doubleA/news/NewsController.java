package com.doubleA.news;

import com.doubleA.crypto.filter.FilterBuilderService;
import com.doubleA.crypto.filter.FilterCondition;
import com.doubleA.crypto.filter.GenericFilterCriteriaBuilder;
import com.doubleA.crypto.filter.PageResponse;
import com.doubleA.news.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@Slf4j
public class NewsController {

    private final NewsService newsService;
    private final FilterBuilderService filterBuilderService;

    public NewsController(NewsService newsService, FilterBuilderService filterBuilderService) {
        this.newsService = newsService;
        this.filterBuilderService = filterBuilderService;
    }


    @GetMapping("/update")
    public ResponseEntity<?> updateData() {
        log.info("[Get][NewsController] Request to method 'updateData'");
        try {
            newsService.updateData();
            return ResponseEntity.ok("Updated");
        } catch (Exception e) {
            log.error("Error in method 'updateData': " + e.getMessage());
            return ResponseEntity.badRequest().body("Not updated");
        }
    }

    @GetMapping
    public ResponseEntity<?> getSearchCriteriaPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "filterAnd", required = false) String filterAnd,
            @RequestParam(value = "orders", required = false) String orders) {

        log.info("[Get][NewsController] Request to method 'getSearchCriteriaPage'");
        try {
            PageResponse<News> response = new PageResponse<>();

            Pageable pageable = filterBuilderService.getPageable(size, page, orders);
            GenericFilterCriteriaBuilder filterCriteriaBuilder = new GenericFilterCriteriaBuilder();

            List<FilterCondition> andConditions = filterBuilderService.createFilterCondition(filterAnd);

            Query query = filterCriteriaBuilder.addCondition(andConditions, new ArrayList<>());
            Page<News> pg = newsService.getPage(query, pageable);
            response.setPageStats(pg, pg.getContent());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in method 'getSearchCriteriaPage': " + e.getMessage());
            return ResponseEntity.badRequest().body(e);
        }
    }
}
