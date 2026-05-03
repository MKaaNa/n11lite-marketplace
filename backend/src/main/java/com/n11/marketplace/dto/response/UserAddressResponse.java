package com.n11.marketplace.dto.response;

public class UserAddressResponse {

    private Long id;
    private String label;
    private String fullAddress;
    private boolean defaultAddress;

    public UserAddressResponse() {
    }

    public UserAddressResponse(Long id, String label, String fullAddress, boolean defaultAddress) {
        this.id = id;
        this.label = label;
        this.fullAddress = fullAddress;
        this.defaultAddress = defaultAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
