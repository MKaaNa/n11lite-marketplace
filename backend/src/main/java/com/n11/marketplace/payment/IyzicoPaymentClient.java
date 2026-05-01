package com.n11.marketplace.payment;

import com.iyzipay.Options;
import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.CheckoutForm;
import com.iyzipay.model.CheckoutFormInitialize;
import com.iyzipay.model.Currency;
import com.iyzipay.model.Locale;
import com.iyzipay.model.PaymentGroup;
import com.iyzipay.model.Status;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.iyzipay.request.RetrieveCheckoutFormRequest;
import com.n11.marketplace.config.IyzicoProperties;
import com.n11.marketplace.entity.Order;
import com.n11.marketplace.entity.OrderItem;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IyzicoPaymentClient {

    private final IyzicoProperties properties;

    public IyzicoPaymentClient(IyzicoProperties properties) {
        this.properties = properties;
    }

    public boolean isConfigured() {
        return properties.isConfigured();
    }

    public CheckoutInitializeResult initializeCheckout(Order order) {
        CheckoutFormInitialize checkoutForm = CheckoutFormInitialize.create(createInitializeRequest(order), options());
        boolean success = Status.SUCCESS.getValue().equals(checkoutForm.getStatus());

        return new CheckoutInitializeResult(
                success,
                checkoutForm.getToken(),
                checkoutForm.getPaymentPageUrl(),
                checkoutForm.getErrorMessage());
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

    private CreateCheckoutFormInitializeRequest createInitializeRequest(Order order) {
        CreateCheckoutFormInitializeRequest request = new CreateCheckoutFormInitializeRequest();
        request.setLocale(Locale.TR.getValue());
        request.setConversationId(String.valueOf(order.getId()));
        request.setPrice(order.getTotalAmount());
        request.setPaidPrice(order.getTotalAmount());
        request.setCurrency(Currency.TRY.name());
        request.setBasketId("ORDER-" + order.getId());
        request.setPaymentGroup(PaymentGroup.PRODUCT.name());
        request.setCallbackUrl(properties.getCallbackUrl());
        request.setEnabledInstallments(List.of(1));

        Buyer buyer = new Buyer();
        buyer.setId(String.valueOf(order.getUser().getId()));
        buyer.setName(order.getUser().getFullName());
        buyer.setSurname("User");
        buyer.setEmail(order.getUser().getEmail());
        buyer.setGsmNumber(order.getUser().getPhone());
        buyer.setIdentityNumber("11111111111");
        buyer.setRegistrationAddress(order.getShippingAddress());
        buyer.setIp("127.0.0.1");
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        buyer.setZipCode("34000");
        request.setBuyer(buyer);

        Address address = new Address();
        address.setContactName(order.getUser().getFullName());
        address.setCity("Istanbul");
        address.setCountry("Turkey");
        address.setAddress(order.getShippingAddress());
        address.setZipCode("34000");
        request.setShippingAddress(address);
        request.setBillingAddress(address);

        List<BasketItem> basketItems = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            BasketItem basketItem = new BasketItem();
            basketItem.setId(String.valueOf(item.getId()));
            basketItem.setName(item.getProductName());
            basketItem.setCategory1("Marketplace");
            basketItem.setItemType(BasketItemType.PHYSICAL.name());
            basketItem.setPrice(item.getLineTotal());
            basketItems.add(basketItem);
        }
        request.setBasketItems(basketItems);

        return request;
    }

    public static class CheckoutInitializeResult {

        private final boolean success;
        private final String token;
        private final String paymentPageUrl;
        private final String errorMessage;

        public CheckoutInitializeResult(boolean success, String token, String paymentPageUrl, String errorMessage) {
            this.success = success;
            this.token = token;
            this.paymentPageUrl = paymentPageUrl;
            this.errorMessage = errorMessage;
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

        public String getErrorMessage() {
            return errorMessage;
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
