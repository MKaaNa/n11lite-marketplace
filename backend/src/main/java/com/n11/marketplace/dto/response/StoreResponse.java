package com.n11.marketplace.dto.response;

import java.math.BigDecimal;

public class StoreResponse {

    private Long id;
    private String name;
    private String logoUrl;
    private BigDecimal rating;
    private boolean official;

    public StoreResponse() {
    }

    public StoreResponse(Long id, String name, String logoUrl, BigDecimal rating, boolean official) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.rating = rating;
        this.official = official;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }
}
