package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PatchMapping("/admin/orders/{orderUuid}/order-receipt-complete")
    public ResponseDto completeOrderReceipt(@PathVariable("orderUuid") UUID orderUuid) {
        // 1. 재고 확인하기
        // 2. 재고 감소시키고
        // 3. 재고 감소 반영하고
        // 4. 결제 시작
        // 5. 결제 완료
        // 6. 주문 생성하기
        

        orderService.completeOrderReceipt(orderUuid);

        return ResponseDto.builder()
                .code(HttpStatus.OK.name())
                .message("주문 접수 완료가 처리되었습니다.")
                .build();
    }
}
