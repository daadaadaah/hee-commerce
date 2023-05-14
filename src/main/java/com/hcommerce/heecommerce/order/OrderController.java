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

        try {
            orderService.completeOrderReceipt(orderUuid);
        } catch (OrderOverStockException ex) {
            return new ResponseDto(HttpStatus.CONFLICT.name(), ex.getMessage());
        } catch (OrderNotFoundException ex) {
            return new ResponseDto(HttpStatus.NOT_FOUND.name(), ex.getMessage());
        } catch (Exception ex) {
            return new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.name(), "내부 서버 오류가 발생했습니다.");
        }

        return new ResponseDto(HttpStatus.OK.name(), "주문 접수 완료가 처리되었습니다.");
    }
}
