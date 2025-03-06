package com.yusufpeksen.url_shortening.dto;

import lombok.Data;

@Data
public class ShortenRequest {

    private String longUrl;
    private String shortCode;
}
