package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.utils.DateTimeConversionUtils;
import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import com.hcommerce.heecommerce.deal.DealProductQueryRepository;
import com.hcommerce.heecommerce.deal.DiscountType;
import com.hcommerce.heecommerce.deal.TimeDealProductDetail;
import com.hcommerce.heecommerce.inventory.InventoryCommandRepository;
import com.hcommerce.heecommerce.inventory.InventoryQueryRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final RedisTemplate<String, String> redisTemplate;
    private final OrderQueryRepository orderQueryRepository;

    private final OrderCommandRepository orderCommandRepository;

    private final InventoryQueryRepository inventoryQueryRepository;

    private final InventoryCommandRepository inventoryCommandRepository;

    private final DealProductQueryRepository dealProductQueryRepository;

    @Autowired
    public OrderService(
        RedisTemplate<String, String> redisTemplate,
        OrderQueryRepository orderQueryRepository,
        OrderCommandRepository orderCommandRepository,
        InventoryQueryRepository inventoryQueryRepository,
        InventoryCommandRepository inventoryCommandRepository,
        DealProductQueryRepository dealProductQueryRepository
    ) {
        this.redisTemplate = redisTemplate;
        this.orderQueryRepository = orderQueryRepository;
        this.orderCommandRepository = orderCommandRepository;
        this.inventoryQueryRepository = inventoryQueryRepository;
        this.inventoryCommandRepository = inventoryCommandRepository;
        this.dealProductQueryRepository = dealProductQueryRepository;
    }

    /**
     * calculateRealOrderQuantity 는 실제 주문 수량을 계산하는 함수이다.
     *
     * 실제 주문 수량은 감소시킨 후의 재고량, 주문량, 재고 부족 처리 옵션에 따라 달라지고, 경우의 수는 다음과 같다.
     * case 1) 재고량이 0 이하인 경우(예 : 감소시킨 후의 재고량 : -4, 주문량 : 3) : 주문 불가
     * case 2-1) 재고량은 0은 아니지만, 재고량이 주문량보다 적은 경우(예 : 감소시킨 후의 재고량 : -2, 주문량 : 3 -> 기존 재고량 : 1) + ALL_CANCEL : 주문 불가
     * case 2-2) 재고량은 0은 아니지만, 재고량이 주문량보다 적은 경우(예 : 감소시킨 후의 재고량 : -2, 주문량 : 3 -> 기존 재고량 : 1) + PARTIAL_ORDER : 주문 가능
     * case 3) 재고량이 주문량보다 많은 경우(예 : 감소시킨 후의 재고량 : 1, 주문량 : 3 -> 기존 재고 : 4) : 주문 가능
     *
     * @param inventoryAfterDecrease : 감소시킨 후의 재고량
     * @param orderQuantity : 주문량
     * @param outOfStockHandlingOption : 재고 부족 처리 옵션
     * @return realOrderQuantity : 실제 주문량
     *
     * realOrderQuantity 이 필요한 이유는 "부분 주문" 때문이다.
     * 재고량이 0은 아니지만, 사용자가 주문한 수량에 비해 재고량이 없는 경우가 있다.
     * 이때, 재고량만큼만 주문하도록 할 수 있도록 "부문 주문"이 가능한데, 사용자가 주문한 수량과 혼동되지 않도록 실제 주문하는 수량이라는 의미를 내포하기 위해서 필요하다.
     */
    private int calculateRealOrderQuantity(UUID dealProductUuid, int inventoryAfterDecrease, int orderQuantity, OutOfStockHandlingOption outOfStockHandlingOption) {
        int realOrderQuantity = orderQuantity;

        int inventoryBeforeDecrease = orderQuantity + inventoryAfterDecrease;

        // TODO : 추가로 인터셉터에서 재고량 검증 하는거 구현해서, 재고가 없는 경우에는 요청이 Redis에 접근이 안되도록 하는건?
        if(
            inventoryBeforeDecrease <= 0 || // case 1
                inventoryBeforeDecrease < orderQuantity && outOfStockHandlingOption == OutOfStockHandlingOption.ALL_CANCEL // case 2-1
        ) {
            inventoryCommandRepository.set(dealProductUuid, inventoryBeforeDecrease);
            // TODO : 재고 이력에 반영

            throw new OrderOverStockException();
        }

        if(inventoryBeforeDecrease > 0 && inventoryBeforeDecrease < orderQuantity && outOfStockHandlingOption == OutOfStockHandlingOption.PARTIAL_ORDER) { // case 2-2
            realOrderQuantity = inventoryBeforeDecrease; // 기존 재고량 만큼만 주문

            inventoryCommandRepository.set(dealProductUuid, 0);
            // TODO : 재고 이력에 반영
        }

        return realOrderQuantity;
    }

    private void saveOrder() {
        // 5-1) 결제 내역
        // 총 결제 금액
        // 결제 유형
        // 결제 날짜
        // 카드 정보 등등

        // 5-2) 주문 데이터

        // 5-3) 재고
    }

    /**
     * rollbackReducedInventory 는 임의로 감소시킨 재고량을 다시 원상복귀하기 위한 함수이다.
     * 함수로 만든 이유는 다양한 원인으로 재고량을 rollback 시켜줘야 하므로, 함수로 만들어 재활용하고 싶었기 때문이다.
     * @param dealProductUuid : 원상복귀해야 하는 딜 상품 key
     * @param amount : 원상복귀해야 하는 재고량
     */
    private void rollbackReducedInventory(UUID dealProductUuid, int amount) {
        inventoryCommandRepository.increaseByAmount(dealProductUuid, amount);
    }

    /**
     * placeOrderInAdvance 는 주문 승인 전에 검증을 위해 미리 주문 내역을 저장하는 함수이다.
     */
    public UUID placeOrderInAdvance(OrderForm orderForm) {
        UUID dealProductUuid = orderForm.getDealProductUuid();

        int orderQuantity = orderForm.getOrderQuantity();

        // 1. DB에 존재하는 dealProductUuid 인지
        validateHasDealProductUuid(dealProductUuid);

        // 2. DB에 존재하는 userId 인지
        // TODO : 회원 기능 추가 후 구현

        // 3. 최대 주문 수량에 맞는 orderQuantity 인지
        validateOrderQuantityInMaxOrderQuantityPerOrder(dealProductUuid, orderQuantity);

        // 4. 재고 사전 감소
        int inventoryAfterDecrease = inventoryCommandRepository.decreaseByAmount(dealProductUuid, orderQuantity); // 무조건 감소라서, 데이터 정합성을 위해 주문 처리가 안된 경우에는 다시 증가시켜줘야 하지 않나?
        // 재고 이력에 반영

        // 5. 실제 주문 가능한 수량 계산
        int realOrderQuantity = 0;

        try {
            OutOfStockHandlingOption outOfStockHandlingOption = orderForm.getOutOfStockHandlingOption();

            realOrderQuantity = calculateRealOrderQuantity(dealProductUuid, inventoryAfterDecrease, orderQuantity, outOfStockHandlingOption);

            // 6. 주문 내역 미리 저장 // TODO : realOrderQuantity == 0 일 때의 예외처리 필요?

            OrderFormSavedInAdvanceEntity orderFormSavedInAdvanceEntity = createOrderFormSavedInAdvanceEntity(orderForm, realOrderQuantity);

            UUID orderUuidSavedInAdvance = orderCommandRepository.saveOrderInAdvance(orderFormSavedInAdvanceEntity);

            return orderUuidSavedInAdvance;
        } catch (OrderOverStockException orderOverStockException) {
            rollbackReducedInventory(dealProductUuid, orderQuantity); // 주문이 가능하든 안하든 사용자가 요청한 주문량 만큼 감소가 일어났으므로,
            throw orderOverStockException;
        } catch (Exception e) {
            /**
             * 예상 예외 발생 경우
             * 1. determineRealOrderQuantity에서 에외가 발생할 때
             * 1) 재고 감소를 위해 Redis 접근하려고 하는데, 안될 때 -> rollback 필요 없음
             * 2) case 1, case 2에 의해 원상 복귀해줘야 할 때 -> rollback 필요함
             *
             * 2. orderCommandRepository.saveOrderInAdvance(orderFormSavedInAdvanceEntity)에서 주문 내역을 저장하려고 MySQL 접근하려고 하는데, 안될 때,
             * -> rollback ? 사용자가 재시도 할 수 있도록 재시도하도록?
             */

            rollbackReducedInventory(dealProductUuid, realOrderQuantity);
            throw e;
        }
    }

    /**
     * calculateRealOrderQuantity 는 실제 주문 수량을 계산하는 함수이다.
     *
     * 실제 주문 수량은 감소시킨 후의 재고량, 주문량, 재고 부족 처리 옵션에 따라 달라지고, 경우의 수는 다음과 같다.
     * case 1) 재고량이 0 이하인 경우(예 : 감소시킨 후의 재고량 : -4, 주문량 : 3) : 주문 불가
     * case 2-1) 재고량은 0은 아니지만, 재고량이 주문량보다 적은 경우(예 : 감소시킨 후의 재고량 : -2, 주문량 : 3 -> 기존 재고량 : 1) + ALL_CANCEL : 주문 불가
     * case 2-2) 재고량은 0은 아니지만, 재고량이 주문량보다 적은 경우(예 : 감소시킨 후의 재고량 : -2, 주문량 : 3 -> 기존 재고량 : 1) + PARTIAL_ORDER : 주문 가능
     * case 3) 재고량이 주문량보다 많은 경우(예 : 감소시킨 후의 재고량 : 1, 주문량 : 3 -> 기존 재고 : 4) : 주문 가능
     *
     * @param dealProductUuid : 감소시킬 딜 상품 Uuid
     * @param orderQuantity : 주문량
     * @param outOfStockHandlingOption : 재고 부족 처리 옵션
     * @return realOrderQuantity : 실제 주문량
     *
     * realOrderQuantity 이 필요한 이유는 "부분 주문" 때문이다.
     * 재고량이 0은 아니지만, 사용자가 주문한 수량에 비해 재고량이 없는 경우가 있다.
     * 이때, 재고량만큼만 주문하도록 할 수 있도록 "부문 주문"이 가능한데, 사용자가 주문한 수량과 혼동되지 않도록 실제 주문하는 수량이라는 의미를 내포하기 위해서 필요하다.
     *
     * Q1) inventoryCommandRepository.set(dealProductUuid, 0) 안해주면 문제가 발생할까?
     * 예를 들어, A 사용자가 요청한 주문 수량은 3개 이고, 부분 주문 옵션을 선택했다.
     * 그런데, 재고는 2개 였다.
     * 그래서, 2개만 주문 가능한 상태이다.
     * 작업 순서는 다음과 같다고 가정해보자.
     * 1. A 사용자의 주문 요청(3개)에 의해 2 - 3 => -1 으로 감소했다.
     * 2. B 사용자의 주문 요청(4개)에 의해 -1 - 4 => -5 인 상태이다.
     * 3. A 사용자의 실제 주문 수량이 2
     * 이때,재고량이 0이 아니지만, -4개인 경우도 0인 경우처럼 주문이 되지 않고, OrderOverStockException가 발생한다.
     * 즉, 0 이하의 값은 어느 값을 갖든지 0과 동일하게 로직이 처리됨. 그래서, 데이터 무결성과 데이터 일관성을 완전히 유지할 필요는 없다고 생각한다.
     * 따라서, inventoryCommandRepository.set(dealProductUuid, 0) 을 해줄 필요가 없다고 판단하여, 제거함.
     *
     * Q2) 데이터의 무결성 또는 일관성이 깨지면 재고 집계할 때 문제되지 않을까?
     * 어짜피 재고가 집계될 때에는 Redis에 있는 재고량을 기준으로 집계하지 않고, 재고 히스토리 테이블을 기준으로 집계하므로, 문제가 되지 않을 거라고 생각한다.
     *
     * Q3) rollback 하는 경우, 문제 없을까? -> 문제가 됨!
     * 작업 순서는 다음과 같다고 가정해보자.
     * 1. A 사용자의 주문 요청(3개)에 의해 2 - 3 => -1 으로 감소했다.
     * 2. B 사용자의 주문 요청(4개)에 의해 -1 - 4 => -5 인 상태이다.
     * 3. A 사용자의 주문 요청에 대한 Rollback(재고량 만큼, 2개) -5 + 2 -> -3 인 상태로 재고가 생겼는데, 없는 것처럼 되네?
     *
     */
    private int determineRealOrderQuantity(UUID dealProductUuid, int orderQuantity, OutOfStockHandlingOption outOfStockHandlingOption) {
        int realOrderQuantity = orderQuantity;

        int inventoryAfterDecrease = inventoryCommandRepository.decreaseByAmount(dealProductUuid, orderQuantity); // 무조건 감소라서, 데이터 정합성을 위해 주문 처리가 안된 경우에는 다시 증가시켜줘야 하지 않나?

        int inventoryBeforeDecrease = orderQuantity + inventoryAfterDecrease;

        // TODO : 추가로 인터셉터에서 재고량 검증 하는거 구현해서, 재고가 없는 경우에는 요청이 Redis에 접근이 안되도록 하는건?
        if(
            inventoryBeforeDecrease <= 0 || // case 1
            inventoryBeforeDecrease < orderQuantity && outOfStockHandlingOption == OutOfStockHandlingOption.ALL_CANCEL // case 2-1
        ) {
            inventoryCommandRepository.set(dealProductUuid, inventoryBeforeDecrease);
            // 재고 이력에 반영

            throw new OrderOverStockException();
        }

        if(inventoryBeforeDecrease > 0 && inventoryBeforeDecrease < orderQuantity && outOfStockHandlingOption == OutOfStockHandlingOption.PARTIAL_ORDER) { // case 2-2
            realOrderQuantity = inventoryBeforeDecrease; // 기존 재고량 만큼만 주문

            inventoryCommandRepository.set(dealProductUuid, 0);
            // 재고 이력에 반영
        }

        return realOrderQuantity;
    }

    /**
     * validateHasDealProductUuid 는 DB에 존재하는 dealProductUuid 인지 검사하는 함수이다.
     */
    private void validateHasDealProductUuid(UUID dealProductUuid) {
        boolean hasDealProductUuid = dealProductQueryRepository.hasDealProductUuid(dealProductUuid);

        if(!hasDealProductUuid) {
            throw new TimeDealProductNotFoundException(dealProductUuid);
        }
    }

    /**
     * validateOrderQuantityInMaxOrderQuantityPerOrder 는 최대 주문 수량에 맞는지에 대해 검증하는 함수이다.
     */
    private void validateOrderQuantityInMaxOrderQuantityPerOrder(UUID dealProductUuid, int orderQuantity) {
        int maxOrderQuantityPerOrder = dealProductQueryRepository.getMaxOrderQuantityPerOrderByDealProductUuid(dealProductUuid);

        if(orderQuantity > maxOrderQuantityPerOrder) {
            throw new MaxOrderQuantityExceededException(maxOrderQuantityPerOrder);
        }
    }

    /**
     * createOrderFormSavedInAdvanceEntity 는 OrderFormSavedInAdvanceEntity 를 만드는 함수 이다.
     * 이 함수가 필요한 이유는 다음 3가지 때문이다.
     * 1. UUID
     * - UUID 는 DB에 저장될 때 byte[] 로 저장되기 때문에, UUID -> byte[] 타입 변환이 필요하다.
     * 2. 부분 주문
     * - 실제 주문 수량과 다르게 주문이 접수되는 경우도 있기 때문이다.
     * 3. 총 결제 금액
     * - 총 결제 금액을 위변조 방지를 위해 클라이언트에서 받은 값이 아닌 DB에 있는 데이터를 기반으로 계산하기 때문이다.
     */
    private OrderFormSavedInAdvanceEntity createOrderFormSavedInAdvanceEntity(OrderForm orderForm, int realOrderQuantity) {
        TimeDealProductDetail timeDealProductDetail = dealProductQueryRepository.getTimeDealProductDetailByDealProductUuid(orderForm.getDealProductUuid());

        int totalPaymentAmount = calculateTotalPaymentAmount(timeDealProductDetail.getProductOriginPrice(), realOrderQuantity, timeDealProductDetail.getDealProductDiscountType(), timeDealProductDetail.getDealProductDiscountValue());

        int originalOrderQuantityForPartialOrder = -1; // 부분 주문이 아닌 경우 값으로, Null 을 넣어주고 싶었으나, int 는 Null 을 헝용하지 않기 때문에, 임의의 값 넣어줌

        if(orderForm.getOutOfStockHandlingOption() == OutOfStockHandlingOption.PARTIAL_ORDER) {
            originalOrderQuantityForPartialOrder = orderForm.getOrderQuantity();
        }

        return OrderFormSavedInAdvanceEntity.builder()
            .uuid(TypeConversionUtils.convertUuidToBinary(orderForm.getOrderUuid()))
            .userId(orderForm.getUserId())
            .recipientInfoForm(orderForm.getRecipientInfoForm())
            .outOfStockHandlingOption(orderForm.getOutOfStockHandlingOption())
            .dealProductUuid(TypeConversionUtils.convertUuidToBinary(orderForm.getDealProductUuid()))
            .totalPaymentAmount(totalPaymentAmount)
            .originalOrderQuantityForPartialOrder(originalOrderQuantityForPartialOrder)
            .realOrderQuantity(realOrderQuantity)
            .paymentMethod(orderForm.getPaymentMethod())
            .build();
    }

    /**
     * calculateTotalPaymentAmount 는 총 결제 금액을 계산하는 함수이다.
     * TODO : 할인 정책이 회원마다 다를 수 있고, 날짜마다, 또는 중복 할인 안되는 등 다양한 경우의 수가 있을 수 있는데, 이부분은 추후에 시간 나면 하기
     */
    private int calculateTotalPaymentAmount(int originPrice, int realOrderQuantity, DiscountType discountType, int discountValue) {
        if (discountType == DiscountType.PERCENTAGE) {
            return (originPrice * ((100 - discountValue) / 100)) * realOrderQuantity;
        }

        return (originPrice - discountValue) * realOrderQuantity; // 정률 할인
    }

    /**
     * approveOrder 는 주문 승인을 하기 위한 함수이다.
     */
    public UUID approveOrder(OrderApproveForm orderApproveForm) {
        String orderId = orderApproveForm.getOrderId();

        // 0. DB에서 검증에 필요한 데이터 가져오기
        OrderEntityForOrderApproveValidation orderForm = orderQueryRepository.findOrderEntityForOrderApproveValidation(orderApproveForm.getOrderId());

        // 1. orderApproveForm 검증
        validateOrderApproveForm(orderApproveForm, orderForm);

        // 3. 토스 페이먼트 결제 승인
        String approvedAt = "2022-01-01T00:00:00+09:00"; // TODO : 임시 데이터

        // 4. 주문 관련 데이터 저장
        byte[] orderUuid = TypeConversionUtils.convertUuidToBinary(UUID.fromString(orderId));

        OrderApproveEntity orderApproveEntity = OrderApproveEntity.builder()
            .realOrderQuantity(orderForm.getRealOrderQuantity())
            .paymentApprovedAt(DateTimeConversionUtils.convertIsoDateTimeToInstant(approvedAt))
            .build();

        orderCommandRepository.updateOrderAfterApprove(orderUuid, orderApproveEntity);

        return UUID.fromString(orderId);
    }

    public void validateOrderApproveForm(OrderApproveForm orderApproveForm, OrderEntityForOrderApproveValidation orderForm) {
        if(orderApproveForm.getAmount() != orderForm.getTotalPaymentAmount()) {
            throw new InvalidPaymentAmountException();
        }
    }
}