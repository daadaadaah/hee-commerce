package com.hcommerce.heecommerce.payment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentViewController {

    @GetMapping("/payments")
    public String handlePaymentFormView() {
        return "payment";
    }
}