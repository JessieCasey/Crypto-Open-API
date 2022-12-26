package com.doubleA.crypto.service;

import com.doubleA.crypto.Crypto;
import com.doubleA.crypto.dto.CryptoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface CryptoService {

    List<CryptoDTO> getTrending(int count);

    Crypto getCrypto(String id);

    void updateData() throws URISyntaxException, IOException;

    Map<String, Object> getDataToMakeGraph(String cryptoName, String time);

    Page<Crypto> getPage(Query query, Pageable pageable);

    List<CryptoDTO> getTopMarketCap(int count);

}
