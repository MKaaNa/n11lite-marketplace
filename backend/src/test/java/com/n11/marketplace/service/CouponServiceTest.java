package com.n11.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n11.marketplace.dto.response.CouponResponse;
import com.n11.marketplace.entity.Coupon;
import com.n11.marketplace.enums.DiscountType;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.repository.CouponRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    private CouponService couponService;

    @BeforeEach
    void setUp() {
        couponService = new CouponService(couponRepository);
    }

    @Test
    void validatePercentCouponSuccess() {
        Coupon coupon = createCoupon("N11WELCOME", DiscountType.PERCENT, "10.00");
        when(couponRepository.findByCodeIgnoreCase("N11WELCOME")).thenReturn(Optional.of(coupon));

        CouponResponse response = couponService.validateCoupon("N11WELCOME", new BigDecimal("500.00"));

        assertEquals(new BigDecimal("50.00"), response.getDiscountAmount());
        assertEquals(new BigDecimal("450.00"), response.getFinalTotal());
        assertEquals("PERCENT", response.getDiscountType());
    }

    @Test
    void validateFixedCouponSuccess() {
        Coupon coupon = createCoupon("TECH50", DiscountType.FIXED, "50.00");
        when(couponRepository.findByCodeIgnoreCase("TECH50")).thenReturn(Optional.of(coupon));

        CouponResponse response = couponService.validateCoupon("TECH50", new BigDecimal("300.00"));

        assertEquals(new BigDecimal("50.00"), response.getDiscountAmount());
        assertEquals(new BigDecimal("250.00"), response.getFinalTotal());
    }

    @Test
    void rejectMissingCoupon() {
        when(couponRepository.findByCodeIgnoreCase("MISSING")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> couponService.validateCoupon("MISSING", new BigDecimal("300.00")));

        assertEquals("Coupon not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void rejectInactiveCoupon() {
        Coupon coupon = createCoupon("PASSIVE", DiscountType.FIXED, "50.00");
        coupon.setActive(false);
        when(couponRepository.findByCodeIgnoreCase("PASSIVE")).thenReturn(Optional.of(coupon));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> couponService.validateCoupon("PASSIVE", new BigDecimal("300.00")));

        assertEquals("Coupon is not active", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void rejectExpiredCoupon() {
        Coupon coupon = createCoupon("OLD", DiscountType.FIXED, "50.00");
        coupon.setExpiresAt(LocalDateTime.now().minusDays(1));
        when(couponRepository.findByCodeIgnoreCase("OLD")).thenReturn(Optional.of(coupon));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> couponService.validateCoupon("OLD", new BigDecimal("300.00")));

        assertEquals("Coupon is expired", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void rejectBelowMinOrderAmount() {
        Coupon coupon = createCoupon("MIN300", DiscountType.FIXED, "50.00");
        coupon.setMinOrderAmount(new BigDecimal("300.00"));
        when(couponRepository.findByCodeIgnoreCase("MIN300")).thenReturn(Optional.of(coupon));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> couponService.validateCoupon("MIN300", new BigDecimal("200.00")));

        assertEquals("Cart total is below coupon minimum amount", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void respectMaxDiscountAmountForPercentCoupon() {
        Coupon coupon = createCoupon("MAX", DiscountType.PERCENT, "50.00");
        coupon.setMaxDiscountAmount(new BigDecimal("100.00"));
        when(couponRepository.findByCodeIgnoreCase("MAX")).thenReturn(Optional.of(coupon));

        CouponResponse response = couponService.validateCoupon("MAX", new BigDecimal("500.00"));

        assertEquals(new BigDecimal("100.00"), response.getDiscountAmount());
        assertEquals(new BigDecimal("400.00"), response.getFinalTotal());
    }

    @Test
    void discountCannotMakeFinalTotalNegative() {
        Coupon coupon = createCoupon("BIG", DiscountType.FIXED, "500.00");
        when(couponRepository.findByCodeIgnoreCase("BIG")).thenReturn(Optional.of(coupon));

        CouponResponse response = couponService.validateCoupon("BIG", new BigDecimal("200.00"));

        assertEquals(new BigDecimal("200.00"), response.getDiscountAmount());
        assertEquals(new BigDecimal("0.00"), response.getFinalTotal());
    }

    @Test
    void incrementUsedCount() {
        Coupon coupon = createCoupon("N11WELCOME", DiscountType.PERCENT, "10.00");
        when(couponRepository.findByCodeIgnoreCase("N11WELCOME")).thenReturn(Optional.of(coupon));

        couponService.markCouponUsed("N11WELCOME");

        assertEquals(1, coupon.getUsedCount());
        verify(couponRepository).save(coupon);
    }

    @Test
    void markCouponUsedIncrementsEvenIfExpired() {
        Coupon coupon = createCoupon("OLD", DiscountType.FIXED, "50.00");
        coupon.setExpiresAt(LocalDateTime.now().minusDays(1));
        when(couponRepository.findByCodeIgnoreCase("OLD")).thenReturn(Optional.of(coupon));

        couponService.markCouponUsed("OLD");

        assertEquals(1, coupon.getUsedCount());
        verify(couponRepository).save(coupon);
    }

    @Test
    void markCouponUsedIncrementsEvenIfInactive() {
        Coupon coupon = createCoupon("PASSIVE", DiscountType.FIXED, "50.00");
        coupon.setActive(false);
        when(couponRepository.findByCodeIgnoreCase("PASSIVE")).thenReturn(Optional.of(coupon));

        couponService.markCouponUsed("PASSIVE");

        assertEquals(1, coupon.getUsedCount());
        verify(couponRepository).save(coupon);
    }

    @Test
    void rejectUsageLimitReached() {
        Coupon coupon = createCoupon("LIMITED", DiscountType.FIXED, "50.00");
        coupon.setUsageLimit(10);
        coupon.setUsedCount(10);
        when(couponRepository.findByCodeIgnoreCase("LIMITED")).thenReturn(Optional.of(coupon));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> couponService.validateCoupon("LIMITED", new BigDecimal("300.00")));

        assertEquals("Coupon usage limit reached", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void rejectNotStartedCoupon() {
        Coupon coupon = createCoupon("FUTURE", DiscountType.FIXED, "50.00");
        coupon.setStartsAt(LocalDateTime.now().plusDays(1));
        when(couponRepository.findByCodeIgnoreCase("FUTURE")).thenReturn(Optional.of(coupon));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> couponService.validateCoupon("FUTURE", new BigDecimal("300.00")));

        assertEquals("Coupon is not started yet", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    private Coupon createCoupon(String code, DiscountType type, String value) {
        return new Coupon(code, type, new BigDecimal(value));
    }
}
