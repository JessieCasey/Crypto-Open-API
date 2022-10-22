package com.doubleA.crypto;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoRepository extends MongoRepository<Crypto, String> {
}
