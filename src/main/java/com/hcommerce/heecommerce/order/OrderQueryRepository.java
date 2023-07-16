package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import com.hcommerce.heecommerce.order.entity.OrderForOrderApproveValidationEntity;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderQueryRepository {

    private final OrderQueryMapper orderQueryMapper;

    @Autowired
    public OrderQueryRepository(OrderQueryMapper orderQueryMapper) {
        this.orderQueryMapper = orderQueryMapper;
    }

    public OrderForOrderApproveValidationEntity findOrderEntityForOrderApproveValidation(String orderId) {

        UUID orderUuid = UUID.fromString(orderId);

        byte[] orderUuidByte = TypeConversionUtils.convertUuidToBinary(orderUuid);

        return orderQueryMapper.findOrderEntityForOrderApproveValidation(orderUuidByte);
    }
}
