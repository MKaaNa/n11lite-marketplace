package com.n11.marketplace.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "iyzico")
public class IyzicoProperties {

    private String apiKey;
    private String secretKey;
    private String baseUrl;
    private String callbackUrl;

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank()
                && secretKey != null && !secretKey.isBlank()
                && baseUrl != null && !baseUrl.isBlank()
                && callbackUrl != null && !callbackUrl.isBlank();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}
