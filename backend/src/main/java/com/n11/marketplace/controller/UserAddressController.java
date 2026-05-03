package com.n11.marketplace.controller;

import com.n11.marketplace.dto.request.UpsertUserAddressRequest;
import com.n11.marketplace.dto.response.UserAddressResponse;
import com.n11.marketplace.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/addresses")
@Tag(name = "My addresses")
public class UserAddressController {

    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @GetMapping
    @Operation(summary = "List saved delivery addresses")
    public ResponseEntity<List<UserAddressResponse>> list(Principal principal) {
        return ResponseEntity.ok(userAddressService.list(principal.getName()));
    }

    @PostMapping
    @Operation(summary = "Save a delivery address")
    public ResponseEntity<UserAddressResponse> create(
            Principal principal, @Valid @RequestBody UpsertUserAddressRequest request) {
        return ResponseEntity.ok(userAddressService.create(principal.getName(), request));
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update a delivery address")
    public ResponseEntity<UserAddressResponse> update(
            Principal principal,
            @PathVariable Long addressId,
            @Valid @RequestBody UpsertUserAddressRequest request) {
        return ResponseEntity.ok(userAddressService.update(principal.getName(), addressId, request));
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete a delivery address")
    public ResponseEntity<Void> delete(Principal principal, @PathVariable Long addressId) {
        userAddressService.delete(principal.getName(), addressId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{addressId}/default")
    @Operation(summary = "Set default delivery address")
    public ResponseEntity<UserAddressResponse> setDefault(Principal principal, @PathVariable Long addressId) {
        return ResponseEntity.ok(userAddressService.setDefault(principal.getName(), addressId));
    }
}
