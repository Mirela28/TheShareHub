package com.thesharehub.TheShareHub.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;


@RestController
@Profile("!test")
@CrossOrigin("http://localhost:3000")
@RequestMapping("/payments")
public class PaymentController {

//    @Value("${stripe.secret}")
//    private String stripeSecret;
//
//    @PostMapping("/createpayment")
//    public Map<String, String> createPayment(@RequestBody Map<String, Object> data) throws StripeException {
//        com.stripe.Stripe.apiKey = stripeSecret;
//
//        Long amount = ((Number) data.get("amount")).longValue();
//
//        PaymentIntentCreateParams params = PaymentIntentCreateParams
//                .builder()
//                .setAmount(amount)
//                .setCurrency("eur")
//                .build();
//
//        PaymentIntent intent = PaymentIntent.create(params);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("clientSecret", intent.getClientSecret());
//        return response;
//    }


}
