package com.doubleA.apikey;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends MongoRepository<ApiKey, String> {

}
