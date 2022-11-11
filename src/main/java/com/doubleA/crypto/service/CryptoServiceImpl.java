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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<CryptoDTO> getTrending() {
        String uri = "https://api.coingecko.com/api/v3/search/trending";

        List<CryptoDTO> response = new ArrayList<>();

        new JSONObject(makeAPICall(uri, new ArrayList<>())).getJSONArray("coins").forEach(x -> {
            JSONObject next = ((JSONObject) x).getJSONObject("item");
            response.add(new CryptoDTO(
                    next.getString("id"), next.getString("name"),
                    next.getString("small"), next.getString("id"), next.getFloat("price_btc")));
        });

        return response;
    }

    @Override
    public Crypto getCrypto(String key) {
        return cryptoRepository.findById(key).orElseThrow();
    }

    @Override
    public void updateData() {
        String uri = "https://api.coingecko.com/api/v3/coins/markets";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("vs_currency", "usd"));

        final JSONArray data = new JSONArray(makeAPICall(uri, params));

        for (int i = 0; i < data.length(); ++i) {
            final JSONObject cryptoJSON = data.getJSONObject(i);
            cryptoRepository.save(new Gson().fromJson(String.valueOf(cryptoJSON), Crypto.class));
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
