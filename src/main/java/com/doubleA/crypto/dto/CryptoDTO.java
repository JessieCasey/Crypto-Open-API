package com.doubleA.crypto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CryptoDTO {
    private String id;
    private String name;
    private String symbol;
    private String image;
    private Float price;
}
