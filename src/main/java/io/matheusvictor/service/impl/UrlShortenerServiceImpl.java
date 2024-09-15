package io.matheusvictor.service.impl;

import io.matheusvictor.entity.UrlShortener;
import io.matheusvictor.service.UrlShortenerService;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class UrlShortenerServiceImpl implements UrlShortenerService {
    private static final int MAX_LENGTH = 7;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Map<String, String> urlMap = new HashMap<>();
    private final Map<String, String> reverseMap = new HashMap<>();


    @Override
    @Transactional
    public String shortenUrl(String url) {
        UrlShortener urlShortener = new UrlShortener();
        urlShortener.setOriginalUrl(url);

        String shortCode = generateShortCode();
        urlShortener.setShortUrl(shortCode);

        urlMap.putIfAbsent(shortCode, url);
        reverseMap.putIfAbsent(url, shortCode);

        urlShortener.persist(); // Now this should work correctly

        return "http://localhost:8080/short/" + shortCode;
    }

    @Override
    public String getShortenedUrl(String url) {
        UrlShortener shortenedUrl = UrlShortener.find("shortUrl ", url).firstResult();
        if (shortenedUrl == null) {
            throw new NotFoundException("URL not found: " + url);
        }
        return shortenedUrl.getOriginalUrl();
    }

    private String generateShortCode() {
        UUID uuid = UUID.randomUUID();
        String encoded = Base64.getEncoder().encodeToString(uuid.toString().getBytes());
        String shortCode = String.valueOf(CHARACTERS.charAt(encoded.charAt(0) - 'A')) +
                CHARACTERS.charAt(encoded.charAt(1) - 'A') +
                CHARACTERS.charAt(encoded.charAt(2) - 'A');

        int counter = 1;
        while (urlMap.containsKey(shortCode)) {
            shortCode += CHARACTERS.charAt(counter % CHARACTERS.length());
            counter++;
        }

        return shortCode.substring(0, Math.min(MAX_LENGTH, shortCode.length()));
    }
}

