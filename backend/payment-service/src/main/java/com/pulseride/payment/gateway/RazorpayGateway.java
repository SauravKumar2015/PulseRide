package com.pulseride.payment.gateway;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;
@Component public class RazorpayGateway implements PaymentGateway { public String createOrder(BigDecimal amount,String currency,String paymentId){ return paymentId; } }
