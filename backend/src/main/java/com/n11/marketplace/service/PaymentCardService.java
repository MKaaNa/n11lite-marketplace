package com.n11.marketplace.service;

import com.iyzipay.model.Card;
import com.n11.marketplace.dto.request.RegisterPaymentCardRequest;
import com.n11.marketplace.dto.response.SavedCardResponse;
import com.n11.marketplace.entity.User;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.payment.CardRegistrationResult;
import com.n11.marketplace.payment.IyzicoPaymentClient;
import com.n11.marketplace.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentCardService {

    private final UserRepository userRepository;
    private final IyzicoPaymentClient iyzicoPaymentClient;

    public PaymentCardService(UserRepository userRepository, IyzicoPaymentClient iyzicoPaymentClient) {
        this.userRepository = userRepository;
        this.iyzicoPaymentClient = iyzicoPaymentClient;
    }

    @Transactional(readOnly = true)
    public List<SavedCardResponse> list(String userEmail) {
        User user = findUser(userEmail);
        String key = user.getIyzicoCardUserKey();
        if (key == null || key.isBlank()) {
            return List.of();
        }
        return iyzicoPaymentClient.listCards(key).stream().map(this::toResponse).toList();
    }

    @Transactional
    public SavedCardResponse register(String userEmail, RegisterPaymentCardRequest request) {
        if (!iyzicoPaymentClient.isConfigured()) {
            throw new BusinessException(
                    "Iyzico sandbox bilgileri tanımlı değil; kart kaydı yapılamıyor.", HttpStatus.BAD_REQUEST);
        }
        User user = findUser(userEmail);
        try {
            CardRegistrationResult result = iyzicoPaymentClient.registerCard(user, request);
            if (user.getIyzicoCardUserKey() == null || user.getIyzicoCardUserKey().isBlank()) {
                user.setIyzicoCardUserKey(result.cardUserKey());
                userRepository.save(user);
            }
            return new SavedCardResponse(
                    result.cardToken(),
                    result.cardAlias(),
                    result.binNumber(),
                    result.lastFourDigits(),
                    result.cardType(),
                    result.cardAssociation());
        } catch (IllegalStateException ex) {
            throw new BusinessException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public void delete(String userEmail, String cardToken) {
        if (!iyzicoPaymentClient.isConfigured()) {
            throw new BusinessException("Iyzico yapılandırılmadı.", HttpStatus.BAD_REQUEST);
        }
        User user = findUser(userEmail);
        String key = user.getIyzicoCardUserKey();
        if (key == null || key.isBlank()) {
            throw new BusinessException("Kayıtlı kart yok.", HttpStatus.NOT_FOUND);
        }
        try {
            iyzicoPaymentClient.deleteCard(key, cardToken);
        } catch (IllegalStateException ex) {
            throw new BusinessException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        List<Card> remaining = iyzicoPaymentClient.listCards(key);
        if (remaining.isEmpty()) {
            user.setIyzicoCardUserKey(null);
            userRepository.save(user);
        }
    }

    private SavedCardResponse toResponse(Card card) {
        return new SavedCardResponse(
                card.getCardToken(),
                card.getCardAlias(),
                card.getBinNumber(),
                card.getLastFourDigits(),
                card.getCardType(),
                card.getCardAssociation());
    }

    private User findUser(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.UNAUTHORIZED));
    }
}
