package com.n11.marketplace.service;

import com.n11.marketplace.dto.response.CouponResponse;
import com.n11.marketplace.entity.CartItem;
import com.n11.marketplace.entity.Coupon;
import com.n11.marketplace.entity.OrderItem;
import com.n11.marketplace.enums.DiscountType;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.repository.CouponRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional(readOnly = true)
    public CouponResponse validateCoupon(String code, BigDecimal cartTotal) {
        Coupon coupon = findValidCoupon(code, cartTotal);
        BigDecimal discountAmount = calculateDiscount(coupon, resolveApplicableTotal(coupon, cartTotal));
        BigDecimal finalTotal = cartTotal.subtract(discountAmount).max(BigDecimal.ZERO);

        return new CouponResponse(
                coupon.getCode(),
                coupon.getDiscountType().name(),
                coupon.getDiscountValue(),
                discountAmount,
                cartTotal,
                finalTotal,
                coupon.getProduct() != null ? coupon.getProduct().getSlug() : null,
                "Coupon applied successfully");
    }

    @Transactional(readOnly = true)
    public CouponResponse validateCouponWithCartItems(String code, BigDecimal cartTotal, List<CartItem> cartItems) {
        Coupon coupon = findValidCoupon(code, cartTotal);
        BigDecimal applicableTotal = resolveApplicableTotalForCart(coupon, cartItems);
        BigDecimal discountAmount = calculateDiscount(coupon, applicableTotal);
        BigDecimal finalTotal = cartTotal.subtract(discountAmount).max(BigDecimal.ZERO);

        return new CouponResponse(
                coupon.getCode(),
                coupon.getDiscountType().name(),
                coupon.getDiscountValue(),
                discountAmount,
                cartTotal,
                finalTotal,
                coupon.getProduct() != null ? coupon.getProduct().getSlug() : null,
                "Coupon applied successfully");
    }

    @Transactional(readOnly = true)
    public Coupon findValidCoupon(String code, BigDecimal total) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new BusinessException("Coupon not found", HttpStatus.NOT_FOUND));

        validateCouponRules(coupon, total);
        return coupon;
    }

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal total) {
        BigDecimal discount;

        if (coupon.getDiscountType() == DiscountType.PERCENT) {
            discount = total.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (coupon.getMaxDiscountAmount() != null
                    && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discount = coupon.getMaxDiscountAmount();
            }
        } else {
            discount = coupon.getDiscountValue();
        }

        if (discount.compareTo(total) > 0) {
            discount = total;
        }

        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateDiscountForOrder(Coupon coupon, BigDecimal orderTotal, List<OrderItem> items) {
        BigDecimal applicableTotal = resolveApplicableTotalForOrder(coupon, items);
        BigDecimal discount = calculateDiscount(coupon, applicableTotal);
        if (discount.compareTo(orderTotal) > 0) {
            return orderTotal.setScale(2, RoundingMode.HALF_UP);
        }
        return discount;
    }

    @Transactional
    public void markCouponUsed(String code) {
        if (code == null || code.isBlank()) {
            return;
        }

        Coupon coupon = couponRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new BusinessException("Coupon not found", HttpStatus.NOT_FOUND));
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
    }

    private void validateCouponRules(Coupon coupon, BigDecimal total) {
        validateCouponAvailability(coupon);

        if (coupon.getDiscountType() == DiscountType.PERCENT
                && (coupon.getDiscountValue().compareTo(BigDecimal.ONE) < 0
                || coupon.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new BusinessException("Coupon discount value is invalid", HttpStatus.BAD_REQUEST);
        }

        if (coupon.getMinOrderAmount() != null && total.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new BusinessException("Cart total is below coupon minimum amount", HttpStatus.BAD_REQUEST);
        }
    }

    private BigDecimal resolveApplicableTotal(Coupon coupon, BigDecimal total) {
        if (coupon.getProduct() == null) {
            return total;
        }
        return total;
    }

    private BigDecimal resolveApplicableTotalForCart(Coupon coupon, List<CartItem> items) {
        if (coupon.getProduct() == null) {
            return items.stream()
                    .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        BigDecimal subtotal = items.stream()
                .filter(item -> item.getProduct() != null
                        && item.getProduct().getId().equals(coupon.getProduct().getId()))
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Coupon is valid only for a specific product in cart", HttpStatus.BAD_REQUEST);
        }
        return subtotal;
    }

    private BigDecimal resolveApplicableTotalForOrder(Coupon coupon, List<OrderItem> items) {
        if (coupon.getProduct() == null) {
            return items.stream()
                    .map(OrderItem::getLineTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        BigDecimal subtotal = items.stream()
                .filter(item -> item.getProduct() != null
                        && item.getProduct().getId().equals(coupon.getProduct().getId()))
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Coupon is valid only for a specific product in cart", HttpStatus.BAD_REQUEST);
        }
        return subtotal;
    }

    private void validateCouponAvailability(Coupon coupon) {
        if (!coupon.isActive()) {
            throw new BusinessException("Coupon is not active", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartsAt() != null && coupon.getStartsAt().isAfter(now)) {
            throw new BusinessException("Coupon is not started yet", HttpStatus.BAD_REQUEST);
        }

        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(now)) {
            throw new BusinessException("Coupon is expired", HttpStatus.BAD_REQUEST);
        }

        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new BusinessException("Coupon usage limit reached", HttpStatus.BAD_REQUEST);
        }
    }
}
