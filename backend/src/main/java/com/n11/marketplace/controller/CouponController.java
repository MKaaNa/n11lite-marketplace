package com.n11.marketplace.controller;

import com.n11.marketplace.dto.request.ValidateCouponRequest;
import com.n11.marketplace.dto.response.CouponResponse;
import com.n11.marketplace.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate a coupon for the current cart total")
    public ResponseEntity<CouponResponse> validateCoupon(@Valid @RequestBody ValidateCouponRequest request) {
        return ResponseEntity.ok(couponService.validateCoupon(request.getCode(), request.getCartTotal()));
    }
}
