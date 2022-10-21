package com.doubleA.security.jwt;

import com.doubleA.apikey.ApiKeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CKeyFilter extends GenericFilterBean {

    public static final String APIKEY = "C-KEY";
    private final ApiKeyRepository apiKeyRepository;

    @Autowired
    public CKeyFilter(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        if (req.getRequestURI().matches(".*\\b(auth)\\b.*") || apiKeyRepository.existsById(req.getHeader(APIKEY))) {
            chain.doFilter(request, response);
        } else {
            log.warn("Entered");
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json");
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Required headers not specified in the request");
        }

    }
}