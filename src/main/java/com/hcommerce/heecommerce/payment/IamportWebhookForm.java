package com.hcommerce.heecommerce.payment;

import lombok.Getter;

/**
 * 스네이크 -> 카멜 : https://cbw1030.tistory.com/315
 */
@Getter
public class IamportWebhookForm {
    private final String imp_uid;
    private final String merchant_uid;
    private final String status;

    public IamportWebhookForm(String imp_uid, String merchant_uid, String status) {
        this.imp_uid = imp_uid;
        this.merchant_uid = merchant_uid;
        this.status = status;
    }
}
