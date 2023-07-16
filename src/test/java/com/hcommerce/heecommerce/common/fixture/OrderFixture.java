package com.hcommerce.heecommerce.common.fixture;

import com.hcommerce.heecommerce.order.OrderForm;
import com.hcommerce.heecommerce.order.OutOfStockHandlingOption;
import com.hcommerce.heecommerce.order.PaymentMethod;
import com.hcommerce.heecommerce.order.RecipientInfoForm;
import java.util.UUID;

public class OrderFixture {

    public static final int USER_ID = 1;

    public static final UUID ORDER_UUID = UUID.randomUUID();

    public static final RecipientInfoForm recipientInfoForm = RecipientInfoForm.builder()
        .recipientName("leecommerce")
        .recipientPhoneNumber("01087654321")
        .recipientAddress("서울시 ")
        .recipientDetailAddress("101호")
        .shippingRequest("빠른 배송 부탁드려요!")
        .build();

    public static final UUID DEAL_PRODUCT_UUID = DealProductFixture.DEAL_PRODUCT_UUID;

    public static final int INVENTORY = DealProductFixture.DEAL_PRODUCT_DEAL_QUANTITY;

    public static final int MAX_ORDER_QUANTITY_PER_ORDER = DealProductFixture.MAX_ORDER_QUANTITY_PER_ORDER;

    public static final int ORDER_QUANTITY = 2;

    public static final int ORDER_QUANTITY_OVER_MAX_ORDER_QUANTITY_PER_ORDER = DealProductFixture.MAX_ORDER_QUANTITY_PER_ORDER + 2;

    public static final int ORDER_QUANTITY_OVER_INVENTORY = INVENTORY + 2;

    public static final int INVENTORY_AFTER_DECREASE = 1;

    public static final int INVALID_INVENTORY_AFTER_DECREASE = -1;

    private static OrderForm.OrderFormBuilder orderFormBuilder() {
        return OrderForm.builder()
            .userId(USER_ID)
            .orderUuid(ORDER_UUID)
            .recipientInfoForm(recipientInfoForm)
            .outOfStockHandlingOption(OutOfStockHandlingOption.PARTIAL_ORDER)
            .dealProductUuid(DEAL_PRODUCT_UUID)
            .orderQuantity(ORDER_QUANTITY)
            .paymentMethod(PaymentMethod.CREDIT_CARD);
    }

    public static final OrderForm orderForm = orderFormBuilder().build();

    /**
     * rebuilder 는 상황에 따라 필드 값을 수정할 수 있도록 하기 위해 만든 함수이다.
     */
    public static final OrderForm.OrderFormBuilder rebuilder() {
        return orderFormBuilder();
    }
}
