package com.doubleA.crypto;

import com.doubleA.crypto.filter.ResourceRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoRepository extends ResourceRepository<Crypto, String> {
}
