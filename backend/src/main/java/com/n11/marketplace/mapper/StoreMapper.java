package com.n11.marketplace.mapper;

import com.n11.marketplace.dto.response.StoreResponse;
import com.n11.marketplace.entity.Store;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

    public StoreResponse toResponse(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getLogoUrl(),
                store.getRating(),
                store.isOfficial());
    }
}
