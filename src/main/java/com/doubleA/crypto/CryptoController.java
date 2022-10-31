package com.doubleA.crypto;

import com.doubleA.crypto.filter.FilterBuilderService;
import com.doubleA.crypto.filter.FilterCondition;
import com.doubleA.crypto.filter.GenericFilterCriteriaBuilder;
import com.doubleA.crypto.filter.PageResponse;
import com.doubleA.crypto.service.CryptoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

    @GetMapping("/update")
    public ResponseEntity<?> updateData() {
        log.info("[Get][CryptoController] Request to method 'updateData'");
        try {
            cryptoService.updateData();
            return ResponseEntity.ok("Updated");
        } catch (URISyntaxException | IOException e) {
            log.error("Error in method 'getAdvertisementsByUser': " + e.getMessage());
            return ResponseEntity.badRequest().body("Not updated");
        }
    }

    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingCryptos() {
        log.info("[Get][CryptoController] Request to method 'getTrendingCryptos'");
        try {
            return ResponseEntity.ok(cryptoService.getTrending());
        } catch (Exception e) {
            log.error("Error in method 'getTrendingCryptos': " + e.getMessage());
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> getDetailedCrypto(@PathVariable String key) {
        log.info("[Get][CryptoController] Request to method 'getDetailedCrypto'");
        try {
            return ResponseEntity.ok(cryptoService.getCrypto(key));
        } catch (Exception e) {
            log.error("Error in method 'getDetailedCrypto': " + e.getMessage());
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/graph")
    public ResponseEntity<?> getCryptoGraph(
            @RequestParam(value = "cryptoName", defaultValue = "BTC", required = false) String cryptoName,
            @RequestParam(value = "duration", defaultValue = "DIGITAL_CURRENCY_DAILY", required = false) String duration) {

        log.info("[Get][CryptoController] Request to method 'getCryptoGraph'");
        try {
            return ResponseEntity.ok(cryptoService.getDataToMakeGraph(cryptoName, duration));
        } catch (Exception e) {
            log.error("Error in method 'getCryptoGraph': " + e.getMessage());
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping(value = "/page")
    public ResponseEntity<?> getSearchCriteriaPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "filterAnd", required = false) String filterAnd,
            @RequestParam(value = "orders", required = false) String orders) {

        log.info("[Get][CryptoController] Request to method 'getSearchCriteriaPage'");
        try {
            PageResponse<Crypto> response = new PageResponse<>();

            Pageable pageable = filterBuilderService.getPageable(size, page, orders);
            GenericFilterCriteriaBuilder filterCriteriaBuilder = new GenericFilterCriteriaBuilder();

            List<FilterCondition> andConditions = filterBuilderService.createFilterCondition(filterAnd);

            Query query = filterCriteriaBuilder.addCondition(andConditions, new ArrayList<>());
            Page<Crypto> pg = cryptoService.getPage(query, pageable);
            response.setPageStats(pg, pg.getContent());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in method 'getSearchCriteriaPage': " + e.getMessage());
            return ResponseEntity.badRequest().body(e);
        }
    }
}
