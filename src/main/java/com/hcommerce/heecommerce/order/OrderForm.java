package com.hcommerce.heecommerce.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
public class OrderForm {

    @Range(min = 1, message = "주문자 ID를 확인해주세요.")
    private final int userId;

    @Valid
    @NotNull(message = "수령자 정보는 필수입니다.")
    private RecipientInfoForm recipientInfoForm;

    @NotNull(message = "딜 상품 UUID를 입력해주세요.")
    private final UUID dealProductUuid;

    @Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다.")
    private final int orderQuantity;

    @NotNull(message = "결제 유형을 입력해주세요.")
    private final PaymentType paymentType;

    @Builder
    @ConstructorProperties({
        "userId",
        "recipientInfoForm",
        "dealProductUuid",
        "orderQuantity",
        "paymentType"
    })
    public OrderForm(
        int userId,
        RecipientInfoForm recipientInfoForm,
        UUID dealProductUuid,
        int orderQuantity,
        PaymentType paymentType
    ) {
        this.userId = userId;
        this.recipientInfoForm = recipientInfoForm;
        this.dealProductUuid = dealProductUuid;
        this.orderQuantity = orderQuantity;
        this.paymentType = paymentType;
    }
}
