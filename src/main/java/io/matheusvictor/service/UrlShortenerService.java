package io.matheusvictor.service;

public interface UrlShortenerService {
    String shortenUrl(String url);
    String getShortenedUrl(String url);
}
