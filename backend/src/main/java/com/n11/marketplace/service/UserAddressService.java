package com.n11.marketplace.service;

import com.n11.marketplace.dto.request.UpsertUserAddressRequest;
import com.n11.marketplace.dto.response.UserAddressResponse;
import com.n11.marketplace.entity.User;
import com.n11.marketplace.entity.UserAddress;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.repository.UserAddressRepository;
import com.n11.marketplace.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    public UserAddressService(UserAddressRepository userAddressRepository, UserRepository userRepository) {
        this.userAddressRepository = userAddressRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserAddressResponse> list(String userEmail) {
        findUser(userEmail);
        return userAddressRepository.findByUser_EmailOrderByDefaultAddressDescCreatedAtDesc(userEmail).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UserAddressResponse create(String userEmail, UpsertUserAddressRequest request) {
        User user = findUser(userEmail);
        UserAddress entity = new UserAddress(user, request.getLabel().trim(), request.getFullAddress().trim(), false);
        if (request.isDefaultAddress() || userAddressRepository.findByUser_Email(userEmail).isEmpty()) {
            clearDefaultForUser(userEmail);
            entity.setDefaultAddress(true);
        }
        UserAddress saved = userAddressRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional
    public UserAddressResponse update(String userEmail, Long addressId, UpsertUserAddressRequest request) {
        UserAddress entity = findOwnedAddress(userEmail, addressId);
        entity.setLabel(request.getLabel().trim());
        entity.setFullAddress(request.getFullAddress().trim());
        if (request.isDefaultAddress()) {
            clearDefaultForUser(userEmail);
            entity.setDefaultAddress(true);
        }
        return toResponse(userAddressRepository.save(entity));
    }

    @Transactional
    public void delete(String userEmail, Long addressId) {
        UserAddress entity = findOwnedAddress(userEmail, addressId);
        boolean wasDefault = entity.isDefaultAddress();
        userAddressRepository.delete(entity);
        if (wasDefault) {
            List<UserAddress> remaining = userAddressRepository.findByUser_EmailOrderByDefaultAddressDescCreatedAtDesc(userEmail);
            if (!remaining.isEmpty()) {
                UserAddress next = remaining.get(0);
                next.setDefaultAddress(true);
                userAddressRepository.save(next);
            }
        }
    }

    @Transactional
    public UserAddressResponse setDefault(String userEmail, Long addressId) {
        UserAddress entity = findOwnedAddress(userEmail, addressId);
        clearDefaultForUser(userEmail);
        entity.setDefaultAddress(true);
        return toResponse(userAddressRepository.save(entity));
    }

    private void clearDefaultForUser(String userEmail) {
        List<UserAddress> list = userAddressRepository.findByUser_Email(userEmail);
        for (UserAddress a : list) {
            if (a.isDefaultAddress()) {
                a.setDefaultAddress(false);
            }
        }
        userAddressRepository.saveAll(list);
    }

    private UserAddress findOwnedAddress(String userEmail, Long addressId) {
        return userAddressRepository
                .findByIdAndUser_Email(addressId, userEmail)
                .orElseThrow(() -> new BusinessException("Address not found", HttpStatus.NOT_FOUND));
    }

    private User findUser(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.UNAUTHORIZED));
    }

    private UserAddressResponse toResponse(UserAddress a) {
        return new UserAddressResponse(a.getId(), a.getLabel(), a.getFullAddress(), a.isDefaultAddress());
    }
}
