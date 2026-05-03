package com.n11.marketplace.controller;

import com.n11.marketplace.dto.request.ValidateCouponRequest;
import com.n11.marketplace.dto.response.CouponResponse;
import com.n11.marketplace.service.CouponService;
import com.n11.marketplace.repository.CartRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
@Tag(name = "Coupons")
public class CouponController {

    private final CouponService couponService;
    private final CartRepository cartRepository;

    public CouponController(CouponService couponService, CartRepository cartRepository) {
        this.couponService = couponService;
        this.cartRepository = cartRepository;
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate a coupon for the current cart total")
    public ResponseEntity<CouponResponse> validateCoupon(
            Principal principal,
            @Valid @RequestBody ValidateCouponRequest request) {
        if (principal != null) {
            var cartItems = cartRepository.findByUserEmailWithItemsAndProducts(principal.getName())
                    .map(cart -> cart.getItems())
                    .orElseGet(java.util.List::of);
            return ResponseEntity.ok(
                    couponService.validateCouponWithCartItems(request.getCode(), request.getCartTotal(), cartItems));
        }
        return ResponseEntity.ok(couponService.validateCoupon(request.getCode(), request.getCartTotal()));
    }
}
