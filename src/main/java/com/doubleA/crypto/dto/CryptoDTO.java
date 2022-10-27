package com.doubleA.crypto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CryptoDTO {
    String id;
    String name;
    String symbol;
    String image;
    Float price;

    public CryptoDTO(String id, String name, String symbol, String image, Float price) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.image = image;
        this.price = price;
    }
}
