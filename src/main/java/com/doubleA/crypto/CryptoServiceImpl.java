package com.doubleA.crypto;

import com.doubleA.crypto.dto.CryptoDTO;
import com.google.gson.Gson;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CryptoServiceImpl implements CryptoService {

    private final CryptoRepository cryptoRepository;

    public CryptoServiceImpl(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
    }

    @Override
    public List<Crypto> getListOfCryptos() {
        return cryptoRepository.findAll();
    }

    @Override
    public List<CryptoDTO> getTrending() {
        String uri = "https://api.coingecko.com/api/v3/search/trending";

        List<CryptoDTO> response = new ArrayList<>();

        new JSONObject(makeAPICall(uri, new ArrayList<>())).getJSONArray("coins").forEach(x -> {
            JSONObject next = ((JSONObject) x).getJSONObject("item");
            response.add(new CryptoDTO(next.getString("id"), next.getString("name"), next.getString("small"), next.getString("id"), next.getFloat("price_btc")));
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
    public Page<Crypto> getPage(Query query, Pageable pageable) {
        return cryptoRepository.findAll(query, pageable);
    }

    public static String makeAPICall(String uri, List<NameValuePair> parameters) {
        String response_content = "";

        try {
            URIBuilder query = new URIBuilder(uri);
            query.addParameters(parameters);

            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(query.build());

            request.setHeader(HttpHeaders.ACCEPT, "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                response_content = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return response_content;
    }


}
