package com.doubleA.crypto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CryptoDTO {
    private int number;
    private String name;
    private String symbol;
    private String image;
    private Float change;

    public CryptoDTO(int number, String name, String symbol, String image, Float change) {
        this.number = number;
        this.name = name;
        this.symbol = symbol;
        this.image = image;
        this.change = change;
    }
}
