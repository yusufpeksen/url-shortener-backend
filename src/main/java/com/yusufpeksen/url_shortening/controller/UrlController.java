package com.yusufpeksen.url_shortening.controller;

import com.yusufpeksen.url_shortening.dto.ShortenRequest;
import com.yusufpeksen.url_shortening.model.Url;
import com.yusufpeksen.url_shortening.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody ShortenRequest shortenRequest) {
        if (shortenRequest.getLongUrl() == null || shortenRequest.getShortCode() == null) {
            return ResponseEntity.badRequest().body("LongUrl or ShortCode is null");
        }

        try {
            String shortUrl = urlService.shortenUrl(shortenRequest.getLongUrl(), shortenRequest.getShortCode());
            return ResponseEntity.ok(shortUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Object> redirectToLongUrl(@PathVariable String shortCode) {
        Optional<String> longUrl = urlService.getLongUrl(shortCode);

        return longUrl.map(s -> ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(s))
                .build()).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{shortCode}/clicks")
    public ResponseEntity<Integer> getClickCount(@PathVariable String shortCode) {
        int clickCount = urlService.getClickCount(shortCode);
        return ResponseEntity.ok(clickCount);
    }

    @GetMapping("/url/{shortCode}")
    public ResponseEntity<?> getUrlDetails(@PathVariable String shortCode) {
        Optional<Url> urlOptional = urlService.getUrlDetails(shortCode);

        if (urlOptional.isPresent()) {
            return ResponseEntity.ok(urlOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
