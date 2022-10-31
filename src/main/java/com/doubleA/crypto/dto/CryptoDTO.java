package com.doubleA.crypto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CryptoDTO {
    private String id;
    private String name;
    private String symbol;
    private String image;
    private Float price;
    public CryptoDTO(String id, String name, String symbol, String image, Float price) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.image = image;
        this.price = price;
    }
}
