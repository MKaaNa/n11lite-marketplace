package com.n11.marketplace.service;

import com.n11.marketplace.dto.response.InitiatePaymentResponse;
import com.n11.marketplace.dto.response.PaymentCallbackResponse;
import com.n11.marketplace.dto.response.PaymentResponse;
import com.n11.marketplace.entity.Cart;
import com.n11.marketplace.entity.Order;
import com.n11.marketplace.entity.OrderItem;
import com.n11.marketplace.entity.OrderStatus;
import com.n11.marketplace.entity.Payment;
import com.n11.marketplace.entity.PaymentStatus;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.payment.IyzicoPaymentClient;
import com.n11.marketplace.repository.CartRepository;
import com.n11.marketplace.repository.OrderRepository;
import com.n11.marketplace.repository.PaymentRepository;
import com.n11.marketplace.repository.ProductRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final IyzicoPaymentClient iyzicoPaymentClient;
    private final CouponService couponService;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            IyzicoPaymentClient iyzicoPaymentClient,
            CouponService couponService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.iyzicoPaymentClient = iyzicoPaymentClient;
        this.couponService = couponService;
    }

    @Transactional
    public InitiatePaymentResponse initiateCheckout(String userEmail, Long orderId) {
        checkIyzicoConfigured();
        Order order = orderRepository.findByIdAndUserEmail(orderId, userEmail)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));

        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new BusinessException("Order is not waiting for payment", HttpStatus.BAD_REQUEST);
        }

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElse(new Payment(order, order.getTotalAmount()));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPrice(order.getTotalAmount());

        IyzicoPaymentClient.CheckoutInitializeResult result;
        try {
            result = iyzicoPaymentClient.initializeCheckout(order);
        } catch (IllegalArgumentException ex) {
            log.warn("Iyzico checkout validation failed orderId={} reason={}", order.getId(), ex.getMessage());
            throw new BusinessException(
                    "Ödeme bilgileri eksik veya geçersiz. Lütfen siparişini kontrol edip tekrar dene.",
                    HttpStatus.BAD_REQUEST);
        }

        if (!result.isSuccess()) {
            log.warn(
                    "Iyzico checkout initialize failed orderId={} httpStatus={} providerStatus={} errorCode={} errorMessage={} conversationId={} errorGroup={}",
                    order.getId(),
                    result.getHttpStatus(),
                    result.getStatus(),
                    result.getErrorCode(),
                    result.getErrorMessage(),
                    result.getConversationId(),
                    result.getErrorGroup());
            throw new BusinessException(
                    "Ödeme şu an başlatılamıyor. Lütfen bir süre sonra tekrar dene.",
                    HttpStatus.BAD_REQUEST);
        }

        payment.setIyzicoToken(result.getToken());
        payment.setPaymentPageUrl(result.getPaymentPageUrl());
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Iyzico checkout initiated for order {}, payment {}", order.getId(), savedPayment.getId());

        return new InitiatePaymentResponse(
                order.getId(),
                savedPayment.getId(),
                savedPayment.getIyzicoToken(),
                savedPayment.getPaymentPageUrl(),
                savedPayment.getStatus().name());
    }

    @Transactional
    public PaymentCallbackResponse handleCallback(String token) {
        if (token == null || token.isBlank()) {
            throw new BusinessException("Payment token is required", HttpStatus.BAD_REQUEST);
        }
        checkIyzicoConfigured();

        Payment payment = paymentRepository.findByIyzicoToken(token)
                .orElseThrow(() -> new BusinessException("Payment not found", HttpStatus.NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Payment callback received for already completed payment {}", payment.getId());
            return toCallbackResponse(payment, "Payment already completed");
        }

        IyzicoPaymentClient.CheckoutResult result = iyzicoPaymentClient.retrieveCheckoutResult(token);
        Order order = payment.getOrder();

        if (result.isSuccess()) {
            if (!hasEnoughStock(order)) {
                markFailed(order, payment);
                log.warn("Payment callback failed because stock is not enough for order {}", order.getId());
                return toCallbackResponse(payment, "Payment failed because stock is not enough");
            }

            decreaseStock(order);
            clearUserCart(order);
            couponService.markCouponUsed(order.getCouponCode());
            payment.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.PAID);
            order.setPaymentStatus(PaymentStatus.SUCCESS);
            orderRepository.save(order);
            paymentRepository.save(payment);
            log.info("Payment callback succeeded for order {}, payment {}", order.getId(), payment.getId());
            return toCallbackResponse(payment, "Payment successful");
        }

        markFailed(order, payment);
        log.warn("Payment callback failed for order {}, payment {}", order.getId(), payment.getId());
        return toCallbackResponse(payment, "Payment failed");
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentForOrder(String userEmail, Long orderId) {
        Payment payment = paymentRepository.findByOrderIdAndOrderUserEmail(orderId, userEmail)
                .orElseThrow(() -> new BusinessException("Payment not found", HttpStatus.NOT_FOUND));

        return new PaymentResponse(
                payment.getOrder().getId(),
                payment.getId(),
                payment.getStatus().name(),
                payment.getPaymentPageUrl(),
                payment.getPrice());
    }

    private void checkIyzicoConfigured() {
        if (!iyzicoPaymentClient.isConfigured()) {
            log.warn("Iyzico payment is not configured");
            throw new BusinessException("Iyzico is not configured", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private boolean hasEnoughStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product == null || !product.isActive() || item.getQuantity() > product.getStock()) {
                return false;
            }
        }

        return true;
    }

    private void decreaseStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
    }

    private void clearUserCart(Order order) {
        Optional<Cart> cartOptional = cartRepository.findByUserEmail(order.getUser().getEmail());
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            cart.getItems().clear();
            cartRepository.save(cart);
        }
    }

    private void markFailed(Order order, Payment payment) {
        payment.setStatus(PaymentStatus.FAILED);
        order.setStatus(OrderStatus.FAILED);
        order.setPaymentStatus(PaymentStatus.FAILED);
        orderRepository.save(order);
        paymentRepository.save(payment);
    }

    private PaymentCallbackResponse toCallbackResponse(Payment payment, String message) {
        return new PaymentCallbackResponse(
                payment.getOrder().getId(),
                payment.getId(),
                payment.getStatus().name(),
                payment.getOrder().getStatus().name(),
                message);
    }
}
