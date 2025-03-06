package com.yusufpeksen.url_shortening.service;

import com.yusufpeksen.url_shortening.model.Url;
import com.yusufpeksen.url_shortening.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private static final String BASE_URL = "http://localhost:8080/";
    private static final Pattern SHORT_CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{5,20}$");

    public String shortenUrl(String longUrl, String customShortCode) {
        validateShortCode(customShortCode);

        if (urlRepository.existsByShortCode(customShortCode)) {
            throw new IllegalArgumentException("This short code is already taken.");
        }

        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortCode(customShortCode);

        urlRepository.save(url);
        return BASE_URL + customShortCode;
    }

    public Optional<String> getLongUrl(String shortCode) {
        Optional<Url> urlOptional = urlRepository.findByShortCode(shortCode);

        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);
            return Optional.of(url.getLongUrl());
        }

        return Optional.empty();
    }

    public int getClickCount(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .map(Url::getClickCount)
                .orElse(0);
    }

    public Optional<Url> getUrlDetails(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    private void validateShortCode(String shortCode) {
        if (!StringUtils.hasText(shortCode) || !SHORT_CODE_PATTERN.matcher(shortCode).matches()) {
            throw new IllegalArgumentException("Short code must be between 5-20 characters");
        }
    }
}
