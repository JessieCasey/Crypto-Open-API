package com.doubleA.crypto;

import com.doubleA.crypto.filter.FilterBuilderService;
import com.doubleA.crypto.filter.FilterCondition;
import com.doubleA.crypto.filter.GenericFilterCriteriaBuilder;
import com.doubleA.crypto.filter.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/crypto")
@Slf4j
public class CryptoController {

    private final CryptoService cryptoService;
    private final FilterBuilderService filterBuilderService;

    public CryptoController(CryptoService cryptoService, FilterBuilderService filterBuilderService) {
        this.cryptoService = cryptoService;
        this.filterBuilderService = filterBuilderService;
    }

    @GetMapping
    public ResponseEntity<?> getListCryptos() {
        return ResponseEntity.ok(cryptoService.getListOfCryptos());
    }

    @GetMapping("/update")
    public ResponseEntity<?> updateData() {
        try {
            cryptoService.updateData();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Updated");
    }

    @GetMapping("/trending")
    public ResponseEntity<?> trending() {
        return ResponseEntity.ok(cryptoService.getTrending());
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> getDetailedCrypto(@PathVariable String key) {
        return ResponseEntity.ok(cryptoService.getCrypto(key));
    }

    @GetMapping(value = "/page")
    public ResponseEntity<PageResponse<Crypto>> getSearchCriteriaPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "filterOr", required = false) String filterOr,
            @RequestParam(value = "filterAnd", required = false) String filterAnd,
            @RequestParam(value = "orders", required = false) String orders) {

        PageResponse<Crypto> response = new PageResponse<>();

        Pageable pageable = filterBuilderService.getPageable(size, page, orders);
        GenericFilterCriteriaBuilder filterCriteriaBuilder = new GenericFilterCriteriaBuilder();


        List<FilterCondition> andConditions = filterBuilderService.createFilterCondition(filterAnd);
        List<FilterCondition> orConditions = filterBuilderService.createFilterCondition(filterOr);

        Query query = filterCriteriaBuilder.addCondition(andConditions, orConditions);
        Page<Crypto> pg = cryptoService.getPage(query, pageable);
        response.setPageStats(pg, pg.getContent());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
