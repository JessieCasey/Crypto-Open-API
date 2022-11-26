package com.doubleA.crypto.service;

import com.doubleA.crypto.Crypto;
import com.doubleA.crypto.CryptoRepository;
import com.doubleA.crypto.dto.CryptoDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class CryptoServiceImpl implements CryptoService {

    private final String apiKey;
    private final CryptoRepository cryptoRepository;

    public CryptoServiceImpl(CryptoRepository cryptoRepository,
                             @Value("${alpha.apikey}") String apiKey) {
        this.cryptoRepository = cryptoRepository;
        this.apiKey = apiKey;
    }

    @Override
    public List<CryptoDTO> getTrending(int count) {
        List<Crypto> coins = cryptoRepository.findAll();
        count = ((count == 0 || count > coins.size()) ? coins.size() : count);

        DecimalFormat df = new DecimalFormat("#.##");
        AtomicInteger counter = new AtomicInteger(0);
        return coins.stream()
                .sorted((o1, o2) -> o2.getPriceChangePercentage().compareTo(o1.getPriceChangePercentage()))
                .limit(count).map(
                        x -> new CryptoDTO(counter.incrementAndGet(), x.getName(),
                                x.getSymbol().toUpperCase(), x.getImageURL(),
                                Float.parseFloat(df.format(x.getPriceChangePercentage())))).toList();
    }

    @Override
    public List<CryptoDTO> getTopMarketCap(int count) {
        List<Crypto> coins = cryptoRepository.findAll();
        count = ((count == 0 || count > coins.size()) ? coins.size() : count);

        DecimalFormat df = new DecimalFormat("#.##");
        AtomicInteger counter = new AtomicInteger(0);
        return coins.stream()
                .sorted((o1, o2) -> o2.getMarketCapChangePercentage().compareTo(o1.getMarketCapChangePercentage()))
                .limit(count).map(
                        x -> new CryptoDTO(counter.incrementAndGet(), x.getName(),
                                x.getSymbol().toUpperCase(), x.getImageURL(),
                                Float.parseFloat(df.format(x.getMarketCapChangePercentage())))).toList();
    }

    @Override
    public Crypto getCrypto(String key) {
        return cryptoRepository.findById(key).orElseThrow();
    }

    @Override
    public void updateData() {
        cryptoRepository.deleteAll();
        String url = "https://api.coingecko.com/api/v3/coins/markets";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("vs_currency", "usd"));
        params.add(new BasicNameValuePair("per_page", "250"));
        JSONArray data;

        int pages = 3;
        for (int i = 0; i < pages; i++) {
            params.add(new BasicNameValuePair("page", i + ""));
            data = new JSONArray(makeAPICall(url, params));
            for (int j = 0; j < data.length(); ++j) {
                final JSONObject cryptoJSON = data.getJSONObject(j);
                if (!cryptoJSON.isNull("price_change_percentage_24h") && !cryptoJSON.isNull("market_cap_change_percentage_24h"))
                    cryptoRepository.save(new Gson().fromJson(String.valueOf(cryptoJSON), Crypto.class));
            }
        }

    }

    @Override
    public Map<String, Object> getDataToMakeGraph(String cryptoName, String time) {
        String market = "USD";
        String uri = "https://www.alphavantage.co/query?function="
                + time + "&symbol=" + cryptoName + "&market=" + market + "&apikey=" + apiKey;

        return new Gson().fromJson(
                makeAPICall(uri, new ArrayList<>()),
                new TypeToken<HashMap<String, Object>>() {
                }.getType());
    }

    @Override
    public Page<Crypto> getPage(Query query, Pageable pageable) {
        return cryptoRepository.findAll(query, pageable);
    }

    public static String makeAPICall(String uri, List<NameValuePair> parameters) {
        String responseContent = "";

        try {
            URIBuilder query = new URIBuilder(uri);
            query.addParameters(parameters);

            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(query.build());

            request.setHeader(HttpHeaders.ACCEPT, "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return responseContent;
    }


}
