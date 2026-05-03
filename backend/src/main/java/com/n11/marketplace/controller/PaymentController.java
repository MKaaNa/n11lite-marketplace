package com.n11.marketplace.controller;

import com.n11.marketplace.dto.response.InitiatePaymentResponse;
import com.n11.marketplace.dto.response.PaymentCallbackResponse;
import com.n11.marketplace.dto.response.PaymentResponse;
import com.n11.marketplace.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}/checkout")
    @Operation(summary = "Start Iyzico checkout")
    public ResponseEntity<InitiatePaymentResponse> initiateCheckout(
            Principal principal,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.initiateCheckout(principal.getName(), orderId));
    }

    @PostMapping(value = "/iyzico/callback", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Handle Iyzico callback")
    public ResponseEntity<String> handleIyzicoCallback(
            @RequestParam(required = false) String token) {
        PaymentCallbackResponse response = paymentService.handleCallback(token);
        return ResponseEntity.ok(buildCallbackPage(response));
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Get payment status for order")
    public ResponseEntity<PaymentResponse> getPaymentForOrder(Principal principal, @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentForOrder(principal.getName(), orderId));
    }

    private String buildCallbackPage(PaymentCallbackResponse response) {
        boolean success = "SUCCESS".equals(response.getStatus());
        String title = success ? "Ödeme başarılı" : "Ödeme tamamlanamadı";
        String message = success
                ? "Siparişin başarıyla ödendi ve sipariş durumu güncellendi."
                : "Ödeme işlemi tamamlanamadı. Sepetine dönüp tekrar deneyebilirsin.";
        String badgeClass = success ? "success" : "failed";
        String actionUrl = success ? "http://localhost:5173" : "http://localhost:5173/cart";
        String actionText = success ? "Alışverişe Devam Et" : "Sepete Dön";
        String paymentStatusText = formatPaymentStatus(response.getStatus());
        String orderStatusText = formatOrderStatus(response.getOrderStatus());

        return """
                <!doctype html>
                <html lang="tr">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>N11Lite Ödeme Sonucu</title>
                  <style>
                    :root {
                      color: #172033;
                      background: #f8fafc;
                      font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
                    }
                    * { box-sizing: border-box; }
                    body {
                      min-height: 100vh;
                      margin: 0;
                      display: grid;
                      place-items: center;
                      padding: 24px;
                    }
                    .panel {
                      width: min(520px, 100%%);
                      border: 1px solid #e5e7eb;
                      border-radius: 18px;
                      background: #ffffff;
                      box-shadow: 0 18px 42px rgba(15, 23, 42, 0.10);
                      padding: 34px;
                      text-align: center;
                    }
                    .brand {
                      color: #d71920;
                      font-size: 1.25rem;
                      font-weight: 900;
                      margin-bottom: 20px;
                    }
                    .status-icon {
                      width: 72px;
                      height: 72px;
                      border-radius: 50%%;
                      display: grid;
                      place-items: center;
                      margin: 0 auto 18px;
                      color: #ffffff;
                      font-size: 2rem;
                      font-weight: 900;
                    }
                    .status-icon.success { background: #16a34a; }
                    .status-icon.failed { background: #dc2626; }
                    h1 {
                      margin: 0 0 10px;
                      font-size: 2rem;
                      letter-spacing: 0;
                    }
                    .message {
                      color: #64748b;
                      line-height: 1.6;
                      margin: 0 0 22px;
                    }
                    .summary {
                      display: grid;
                      gap: 10px;
                      border: 1px solid #e2e8f0;
                      border-radius: 12px;
                      background: linear-gradient(180deg, #f8fafc 0%%, #ffffff 100%%);
                      margin-bottom: 22px;
                      padding: 18px;
                      text-align: left;
                    }
                    .summary-row {
                      display: flex;
                      align-items: center;
                      justify-content: space-between;
                      gap: 16px;
                      color: #334155;
                      font-weight: 700;
                      padding: 8px 0;
                    }
                    .summary-row + .summary-row {
                      border-top: 1px solid #e2e8f0;
                    }
                    .summary-row span:first-child {
                      color: #64748b;
                      font-weight: 600;
                    }
                    .status-pill {
                      border-radius: 999px;
                      background: #dcfce7;
                      color: #166534;
                      font-size: 0.86rem;
                      font-weight: 900;
                      padding: 6px 10px;
                    }
                    .status-pill.failed {
                      background: #fee2e2;
                      color: #991b1b;
                    }
                    .helper-note {
                      color: #64748b;
                      font-size: 0.92rem;
                      line-height: 1.5;
                      margin: -8px 0 20px;
                    }
                    .action {
                      display: inline-block;
                      border-radius: 10px;
                      background: #d71920;
                      color: #ffffff;
                      font-weight: 800;
                      padding: 12px 18px;
                      text-decoration: none;
                    }
                  </style>
                </head>
                <body>
                  <main class="panel">
                    <div class="brand">N11Lite</div>
                    <div class="status-icon %s">%s</div>
                    <h1>%s</h1>
                    <p class="message">%s</p>
                    <section class="summary" aria-label="Ödeme özeti">
                      <div class="summary-row"><span>Sipariş No</span><strong>#%d</strong></div>
                      <div class="summary-row"><span>Ödeme No</span><strong>#%d</strong></div>
                      <div class="summary-row"><span>Ödeme Durumu</span><strong class="status-pill %s">%s</strong></div>
                      <div class="summary-row"><span>Sipariş Durumu</span><strong class="status-pill %s">%s</strong></div>
                    </section>
                    <p class="helper-note">Bu sayfayı kapatabilir ya da N11Lite alışveriş ekranına dönebilirsin.</p>
                    <a class="action" href="%s">%s</a>
                  </main>
                </body>
                </html>
                """.formatted(
                badgeClass,
                success ? "✓" : "!",
                title,
                message,
                response.getOrderId(),
                response.getPaymentId(),
                success ? "success" : "failed",
                paymentStatusText,
                success ? "success" : "failed",
                orderStatusText,
                actionUrl,
                actionText);
    }

    private String formatPaymentStatus(String status) {
        return switch (status) {
            case "SUCCESS" -> "Ödeme Başarılı";
            case "FAILED" -> "Ödeme Başarısız";
            case "PENDING" -> "Ödeme Bekliyor";
            default -> status;
        };
    }

    private String formatOrderStatus(String status) {
        return switch (status) {
            case "PAID" -> "Ödendi";
            case "PAYMENT_PENDING" -> "Ödeme Bekliyor";
            case "FAILED" -> "Başarısız";
            case "CANCELLED" -> "İptal Edildi";
            case "SHIPPED" -> "Kargoya Verildi";
            case "DELIVERED" -> "Teslim Edildi";
            default -> status;
        };
    }
}
