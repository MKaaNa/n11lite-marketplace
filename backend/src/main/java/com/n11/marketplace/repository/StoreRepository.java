package com.n11.marketplace.repository;

import com.n11.marketplace.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
