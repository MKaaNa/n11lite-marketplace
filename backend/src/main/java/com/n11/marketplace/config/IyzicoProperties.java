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
        return getApiKey() != null && !getApiKey().isBlank()
                && getSecretKey() != null && !getSecretKey().isBlank()
                && getBaseUrl() != null && !getBaseUrl().isBlank()
                && getCallbackUrl() != null && !getCallbackUrl().isBlank();
    }

    public String getApiKey() {
        return trim(apiKey);
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return trim(secretKey);
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBaseUrl() {
        return stripTrailingSlash(trim(baseUrl));
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCallbackUrl() {
        return trim(callbackUrl);
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String stripTrailingSlash(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String result = value;
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
