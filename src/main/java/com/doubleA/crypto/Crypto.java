package com.doubleA.crypto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document("Cryptos")
@ToString
public class Crypto {
    @Id
    private String id;

    @Indexed(unique = true)
    private String symbol;

    private String name;

    @SerializedName("image")
    private String imageURL;

    @SerializedName("current_price")
    private Float currentPrice;

    @SerializedName("market_cap")
    private Float marketCap;

    @SerializedName("market_cap_rank")
    private Float marketCapRank;

    @SerializedName("total_volume")
    private Float totalVolume;

    @SerializedName("high_24h")
    private Float high24;

    @SerializedName("low_24h")
    private Float low24;

    @SerializedName("price_change_24h")
    private Float priceChange;

    @SerializedName("price_change_percentage_24h")
    private Float priceChangePercentage;

    @SerializedName("market_cap_change_24h")
    private Float marketCapChange;

    @SerializedName("market_cap_change_percentage_24h")
    private Float marketCapChangePercentage;

    @SerializedName("circulating_supply")
    private Float circulatingSupply;

    @SerializedName("total_supply")
    private Float totalSupply;

    @SerializedName("max_supply")
    private Float maxSupply;

    @SerializedName("ath")
    private Float ath;

    @SerializedName("ath_change_percentage")
    private Float athChange;

    @SerializedName("ath_date")
    private Date athDate;

    @SerializedName("atl")
    private Float atl;

    @SerializedName("atl_change_percentage")
    private Float atlChange;

    @SerializedName("atl_date")
    private Date atlDate;

    @SerializedName("last_updated")
    private Date lastUpdated;

}