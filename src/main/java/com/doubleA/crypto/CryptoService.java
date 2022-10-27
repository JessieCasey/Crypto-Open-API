package com.doubleA.crypto;

import com.doubleA.crypto.dto.CryptoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface CryptoService {

    List<Crypto> getListOfCryptos();

    List<CryptoDTO> getTrending();

    Crypto getCrypto(String key);

    void updateData() throws URISyntaxException, IOException;

    Page<Crypto> getPage(Query query, Pageable pageable);

}
