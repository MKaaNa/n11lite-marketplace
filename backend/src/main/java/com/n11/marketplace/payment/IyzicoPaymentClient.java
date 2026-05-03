package com.n11.marketplace.payment;

import com.iyzipay.Options;
import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.Card;
import com.iyzipay.model.CardInformation;
import com.iyzipay.model.CardList;
import com.iyzipay.model.CheckoutForm;
import com.iyzipay.model.CheckoutFormInitialize;
import com.iyzipay.model.Currency;
import com.iyzipay.model.Locale;
import com.iyzipay.model.PaymentGroup;
import com.iyzipay.model.Status;
import com.iyzipay.request.CreateCardRequest;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.iyzipay.request.DeleteCardRequest;
import com.iyzipay.request.RetrieveCardListRequest;
import com.iyzipay.request.RetrieveCheckoutFormRequest;
import com.n11.marketplace.config.IyzicoProperties;
import com.n11.marketplace.dto.request.RegisterPaymentCardRequest;
import com.n11.marketplace.entity.Order;
import com.n11.marketplace.entity.OrderItem;
import com.n11.marketplace.entity.User;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class IyzicoPaymentClient {

    private static final String IYZICO_EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final BigDecimal INSTALLMENT_THRESHOLD = new BigDecimal("5000.00");

    private final IyzicoProperties properties;

    public IyzicoPaymentClient(IyzicoProperties properties) {
        this.properties = properties;
    }

    public boolean isConfigured() {
        return properties.isConfigured();
    }

    public CheckoutInitializeResult initializeCheckout(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order has no line items");
        }

        String callbackUrl = properties.getCallbackUrl();
        if (callbackUrl == null || callbackUrl.isBlank()) {
            throw new IllegalArgumentException("Iyzico callback URL is missing");
        }

        BigDecimal paymentTotal = money(order.getTotalAmount());
        if (paymentTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order total must be positive");
        }

        CreateCheckoutFormInitializeRequest request = createInitializeRequest(order, paymentTotal);
        assertBasketMatchesTotal(request, paymentTotal);
        assertBuyerFieldsPresent(request);

        CheckoutFormInitialize checkoutForm = CheckoutFormInitialize.create(request, options());
        boolean success = Status.SUCCESS.getValue().equals(checkoutForm.getStatus());

        return new CheckoutInitializeResult(
                success,
                checkoutForm.getToken(),
                checkoutForm.getPaymentPageUrl(),
                checkoutForm.getStatus(),
                checkoutForm.getErrorCode(),
                checkoutForm.getErrorMessage(),
                checkoutForm.getConversationId(),
                checkoutForm.getErrorGroup(),
                null);
    }

    public CheckoutResult retrieveCheckoutResult(String token) {
        RetrieveCheckoutFormRequest request = new RetrieveCheckoutFormRequest();
        request.setLocale(Locale.TR.getValue());
        request.setToken(token);

        CheckoutForm checkoutForm = CheckoutForm.retrieve(request, options());
        boolean success = Status.SUCCESS.getValue().equals(checkoutForm.getStatus())
                && "SUCCESS".equalsIgnoreCase(checkoutForm.getPaymentStatus());

        return new CheckoutResult(success, checkoutForm.getPaymentStatus(), checkoutForm.getErrorMessage());
    }

    private Options options() {
        Options options = new Options();
        options.setApiKey(properties.getApiKey());
        options.setSecretKey(properties.getSecretKey());
        options.setBaseUrl(properties.getBaseUrl());
        return options;
    }

    private CreateCheckoutFormInitializeRequest createInitializeRequest(Order order, BigDecimal paymentTotal) {
        CreateCheckoutFormInitializeRequest request = new CreateCheckoutFormInitializeRequest();
        request.setLocale(Locale.TR.getValue());
        request.setConversationId(String.valueOf(order.getId()));
        request.setPrice(paymentTotal);
        request.setPaidPrice(paymentTotal);
        request.setCurrency(Currency.TRY.name());
        request.setBasketId("ORDER-" + order.getId());
        request.setPaymentGroup(PaymentGroup.PRODUCT.name());
        request.setCallbackUrl(properties.getCallbackUrl());
        request.setEnabledInstallments(resolveInstallments(paymentTotal));

        String cardUserKey = order.getUser().getIyzicoCardUserKey();
        if (cardUserKey != null && !cardUserKey.isBlank()) {
            request.setCardUserKey(cardUserKey.trim());
        }

        Buyer buyer = new Buyer();
        buyer.setId(String.valueOf(order.getUser().getId()));
        buyer.setName(getBuyerFirstName(order));
        buyer.setSurname(getBuyerSurname(order));
        buyer.setEmail(getBuyerEmail(order));
        buyer.setGsmNumber(normalizeBuyerGsm(order));
        buyer.setIdentityNumber("11111111111");
        buyer.setRegistrationAddress(getShippingAddress(order));
        buyer.setIp("127.0.0.1");
        buyer.setCity(fallbackCity());
        buyer.setCountry(fallbackCountry());
        buyer.setZipCode(fallbackZip());
        request.setBuyer(buyer);

        Address address = new Address();
        address.setContactName(getBuyerFirstName(order) + " " + getBuyerSurname(order));
        address.setCity(fallbackCity());
        address.setCountry(fallbackCountry());
        address.setAddress(getShippingAddress(order));
        address.setZipCode(fallbackZip());
        request.setShippingAddress(address);
        request.setBillingAddress(address);

        List<BasketItem> basketItems = new ArrayList<>();
        BigDecimal remainingTotal = paymentTotal;
        int idx = 0;
        for (OrderItem item : order.getItems()) {
            BasketItem basketItem = new BasketItem();
            basketItem.setId(String.valueOf(item.getId()));
            basketItem.setName(item.getProductName());
            basketItem.setCategory1("Marketplace");
            basketItem.setItemType(BasketItemType.PHYSICAL.name());
            BigDecimal itemPrice = calculateBasketItemPrice(item, order, paymentTotal, remainingTotal, idx);
            basketItem.setPrice(itemPrice);
            remainingTotal = remainingTotal.subtract(itemPrice);
            basketItems.add(basketItem);
            idx++;
        }
        request.setBasketItems(basketItems);

        return request;
    }

    public CardRegistrationResult registerCard(User user, RegisterPaymentCardRequest body) {
        if (!isConfigured()) {
            throw new IllegalStateException("Iyzico is not configured");
        }
        CreateCardRequest cardRequest = new CreateCardRequest();
        cardRequest.setLocale(Locale.TR.getValue());
        cardRequest.setConversationId("card-" + user.getId() + "-" + System.currentTimeMillis());

        String existingKey = user.getIyzicoCardUserKey();
        if (existingKey != null && !existingKey.isBlank()) {
            cardRequest.setCardUserKey(existingKey.trim());
        } else {
            cardRequest.setEmail(getSafeEmailForIyzico(user.getEmail(), user.getId()));
            cardRequest.setExternalId(String.valueOf(user.getId()));
        }

        CardInformation cardInformation = new CardInformation();
        cardInformation.setCardAlias(body.getCardAlias().trim());
        cardInformation.setCardHolderName(body.getCardHolderName().trim());
        cardInformation.setCardNumber(body.getCardNumber().trim());
        cardInformation.setExpireMonth(body.getExpireMonth().trim());
        cardInformation.setExpireYear(body.getExpireYear().trim());
        cardRequest.setCard(cardInformation);

        Card card = Card.create(cardRequest, options());
        if (!Status.SUCCESS.getValue().equals(card.getStatus())) {
            String msg = card.getErrorMessage() != null ? card.getErrorMessage() : "Kart Iyzico tarafından kaydedilemedi";
            throw new IllegalStateException(msg);
        }
        return new CardRegistrationResult(
                card.getCardUserKey(),
                card.getCardToken(),
                card.getCardAlias(),
                card.getBinNumber(),
                card.getLastFourDigits(),
                card.getCardType(),
                card.getCardAssociation());
    }

    public List<Card> listCards(String cardUserKey) {
        if (!isConfigured()) {
            return Collections.emptyList();
        }
        if (cardUserKey == null || cardUserKey.isBlank()) {
            return Collections.emptyList();
        }
        RetrieveCardListRequest request = new RetrieveCardListRequest();
        request.setLocale(Locale.TR.getValue());
        request.setConversationId("list-" + System.currentTimeMillis());
        request.setCardUserKey(cardUserKey.trim());
        CardList cardList = CardList.retrieve(request, options());
        if (!Status.SUCCESS.getValue().equals(cardList.getStatus()) || cardList.getCardDetails() == null) {
            return Collections.emptyList();
        }
        return cardList.getCardDetails();
    }

    public void deleteCard(String cardUserKey, String cardToken) {
        if (!isConfigured()) {
            throw new IllegalStateException("Iyzico is not configured");
        }
        DeleteCardRequest request = new DeleteCardRequest();
        request.setLocale(Locale.TR.getValue());
        request.setConversationId("del-" + System.currentTimeMillis());
        request.setCardUserKey(cardUserKey.trim());
        request.setCardToken(cardToken.trim());
        Card card = Card.delete(request, options());
        if (!Status.SUCCESS.getValue().equals(card.getStatus())) {
            String msg = card.getErrorMessage() != null ? card.getErrorMessage() : "Kart silinemedi";
            throw new IllegalStateException(msg);
        }
    }

    private void assertBasketMatchesTotal(CreateCheckoutFormInitializeRequest request, BigDecimal paymentTotal) {
        BigDecimal basketSum = request.getBasketItems().stream()
                .map(BasketItem::getPrice)
                .map(this::money)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        basketSum = money(basketSum);
        if (basketSum.compareTo(paymentTotal) != 0) {
            throw new IllegalArgumentException("Basket item total does not match order total");
        }
    }

    private void assertBuyerFieldsPresent(CreateCheckoutFormInitializeRequest request) {
        Buyer b = request.getBuyer();
        if (b == null) {
            throw new IllegalArgumentException("Buyer is missing");
        }
        if (isBlank(b.getEmail()) || isBlank(b.getName()) || isBlank(b.getSurname()) || isBlank(b.getGsmNumber())) {
            throw new IllegalArgumentException("Buyer contact fields are incomplete");
        }
        if (isBlank(b.getRegistrationAddress()) || isBlank(b.getCity()) || isBlank(b.getCountry()) || isBlank(b.getZipCode())) {
            throw new IllegalArgumentException("Buyer address fields are incomplete");
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private String normalizeBuyerGsm(Order order) {
        String raw = order.getUser().getPhone();
        if (raw == null || raw.isBlank()) {
            return "+905350000000";
        }
        String trimmed = raw.trim().replace(" ", "");
        if (trimmed.startsWith("+")) {
            return trimmed;
        }
        String digits = trimmed.replaceAll("\\D", "");
        if (digits.length() == 12 && digits.startsWith("90")) {
            return "+" + digits;
        }
        if (digits.length() == 11 && digits.startsWith("0")) {
            return "+90" + digits.substring(1);
        }
        if (digits.length() == 10) {
            return "+90" + digits;
        }
        return "+905350000000";
    }

    private String getBuyerEmail(Order order) {
        return getSafeEmailForIyzico(order.getUser().getEmail(), order.getUser().getId());
    }

    private String getSafeEmailForIyzico(String email, Long userId) {
        if (email != null) {
            String normalized = email.trim();
            String lowerCased = normalized.toLowerCase();
            boolean reservedDomain = lowerCased.endsWith(".local")
                    || lowerCased.endsWith(".localhost")
                    || lowerCased.endsWith(".test")
                    || lowerCased.endsWith(".invalid")
                    || lowerCased.endsWith(".example");
            if (normalized.matches(IYZICO_EMAIL_REGEX) && !reservedDomain) {
                return normalized;
            }
        }
        if (userId == null) {
            return "guest@n11lite.com";
        }
        return "demo.user" + userId + "@n11lite.com";
    }

    private String getBuyerSurname(Order order) {
        String fullName = order.getUser().getFullName();
        if (fullName == null || fullName.isBlank()) {
            return "User";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return Arrays.stream(parts, 1, parts.length).collect(Collectors.joining(" "));
        }
        return "User";
    }

    private static String fallbackCity() {
        return "Istanbul";
    }

    private static String fallbackCountry() {
        return "Turkey";
    }

    private static String fallbackZip() {
        return "34000";
    }

    private String getBuyerFirstName(Order order) {
        String fullName = order.getUser().getFullName();
        if (fullName == null || fullName.isBlank()) {
            return "N11Lite";
        }

        return fullName.trim().split("\\s+")[0];
    }

    private String getShippingAddress(Order order) {
        String shippingAddress = order.getShippingAddress();
        if (shippingAddress == null || shippingAddress.isBlank()) {
            return "N11Lite Demo Address";
        }

        return shippingAddress.trim();
    }

    private BigDecimal calculateBasketItemPrice(
            OrderItem item,
            Order order,
            BigDecimal paymentTotal,
            BigDecimal remainingTotal,
            int itemIndex) {
        int itemCount = order.getItems().size();
        if (itemIndex == itemCount - 1) {
            return money(remainingTotal);
        }

        BigDecimal originalTotal = order.getItems().stream()
                .map(OrderItem::getLineTotal)
                .map(this::money)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (originalTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return money(money(item.getLineTotal())
                .multiply(paymentTotal)
                .divide(originalTotal, 2, RoundingMode.HALF_UP));
    }

    private BigDecimal money(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private List<Integer> resolveInstallments(BigDecimal paymentTotal) {
        if (paymentTotal != null && paymentTotal.compareTo(INSTALLMENT_THRESHOLD) >= 0) {
            return List.of(1, 2, 3);
        }
        return List.of(1);
    }

    public static class CheckoutInitializeResult {

        private final boolean success;
        private final String token;
        private final String paymentPageUrl;
        private final String status;
        private final String errorCode;
        private final String errorMessage;
        private final String conversationId;
        private final String errorGroup;
        private final Integer httpStatus;

        public CheckoutInitializeResult(boolean success, String token, String paymentPageUrl, String errorMessage) {
            this(success, token, paymentPageUrl, null, null, errorMessage, null, null, null);
        }

        public CheckoutInitializeResult(
                boolean success,
                String token,
                String paymentPageUrl,
                String status,
                String errorCode,
                String errorMessage,
                String conversationId,
                String errorGroup,
                Integer httpStatus) {
            this.success = success;
            this.token = token;
            this.paymentPageUrl = paymentPageUrl;
            this.status = status;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.conversationId = conversationId;
            this.errorGroup = errorGroup;
            this.httpStatus = httpStatus;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getToken() {
            return token;
        }

        public String getPaymentPageUrl() {
            return paymentPageUrl;
        }

        public String getStatus() {
            return status;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getConversationId() {
            return conversationId;
        }

        public String getErrorGroup() {
            return errorGroup;
        }

        public Integer getHttpStatus() {
            return httpStatus;
        }
    }

    public static class CheckoutResult {

        private final boolean success;
        private final String paymentStatus;
        private final String errorMessage;

        public CheckoutResult(boolean success, String paymentStatus, String errorMessage) {
            this.success = success;
            this.paymentStatus = paymentStatus;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
