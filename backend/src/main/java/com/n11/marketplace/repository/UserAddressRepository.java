package com.n11.marketplace.repository;

import com.n11.marketplace.entity.UserAddress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findByUser_EmailOrderByDefaultAddressDescCreatedAtDesc(String userEmail);

    Optional<UserAddress> findByIdAndUser_Email(Long id, String userEmail);

    List<UserAddress> findByUser_Email(String userEmail);
}
