package com.hcommerce.heecommerce.payment;

import com.hcommerce.heecommerce.common.dto.ResponseDto;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private IamportClient iamportClient;

    @Autowired
    public PaymentController(IamportClient iamportClient) {
        this.iamportClient = iamportClient;
    }

    @PostMapping("/payments/complete")
    public ResponseDto completePayment(@RequestBody String impUid) throws IamportResponseException, IOException {

        /**
         * 1. 클라이언트 -> 서버 : 임시로 주문 데이터 저장(상태 : 결제 대기 중)
         * 2. 클라이언트 -> 아임포트 서버 : 결제 요청
         * 3-1. (결제 완료) 아임포트 서버 -> 서버 (webhook)
         * 4. 클라이언트 <- 아임포트 서버 : 302 redirect
         * 5. [V] 클라이언트 -> 서버 : 결제 완료 정보 전달
         *
         * 3-2. (결제 완료) 클라이언트 <- 아임포트 서버 : 결제 실패 메시지
         */




        // 결제 완료 From
        //


        // 사용자 요청 vs 아임포트 정보


        // 사용자
        // 상품
        // 결제


        IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(impUid);



        return ResponseDto.builder()
            .code(HttpStatus.OK.name())
            .message("주문 접수가 완료되었습니다.")
            .data(iamportResponse)
            .build();
    }

    /**
     * POST /payments/webhook/iamport
     *
     * 포트원 서버(iamport 서버)에서 클라이언트 응답을 전달할 때,
     * Wi-Fi 연결 끊김, 혹은 브라우저 자동 리로드 등의 이유로 클라이언트에서 결제 완료에 대한 응답을 받지 못하는 경우가 간헐적으로 발생한다.
     * 이런 경우를 대비해서 포트원 서버(iamport 서버)에서 가맹점 서버(hee-commerce 서버)로 Webhook 이벤트를 발송하여
     * 결제 정보를 동기화할 수 있도록 하는데, 이때 필요한 Webhook Endpoint URL
     *
     * 단, 결제 실패 시에는 Webhook이 호출되지 않는다.
     *
     * 또한, 기본적으로 포트원 서버에서 webhook이 호출되면 가맹점 응답을 기다리지 않고 클라이언트에 302 redirect 응답을 보내기 때문에 결과 도달에 대한 순서를 보장하지 않는다.
     * 그러나, 요청하면, webhook 호출 이후에 클라이언트에 302 redirect 또는 callback 응답을 보내어 순서를 보장 할 수 있다.
     *
     * 문의사항
     * 1. 테스트 결제 시에도 순서를 보장해 줄 수 있는지
     * 2. webhook 받은 에서 검증이 유효하지 않으면, 에러가 나는데, 알아서 클라이언트한테 전달 되는건지, 아니면 가맹점 서버에서 처리해야하는건지
     * 3. 결제결과 DB 처리는 웹훅(Webhook)을 연동하여 수신되는 데이터를 기준으로 처리하셔야 결제결과 누락없이 안정적인 결과처리를 완료하실 수 있습니다. -> 무슨 말이지?
     * 4. 결제정보를 받은 가맹점 endpoint URL 에 대한 POST 요청을 수신하는 예제보면, 결제결과 서버 수신이라고 되 어 있는데,
     * -> https://developers.portone.io/docs/ko/auth/guide/5/post
     * 참고 : https://developers.portone.io/docs/ko/result/webhook
     */

    // "imp_uid": "imp_1234567890", "merchant_uid": "order_id_8237352", "status": "paid"
    @PostMapping("/payments/iamprot-webhook")
    public ResponseDto handleIamportWebhook(@RequestBody IamportWebhookForm iamportWebhookForm) {
        // 1. 검증
        // https://developers.portone.io/docs/ko/auth/guide/5/readme
        // 1-1. 사용자
        // 1-2. 주문한 상품
        // 1-3. 주문한 금액
        // 1-4. 결제 금액


        // 2.


        // 아임포트 정보 vs 우리 DB 정보


        // 사용자
        // 상품
        // 결제
        String impUid = iamportWebhookForm.getImp_uid();

//        IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(impUid);



        return ResponseDto.builder()
            .code(HttpStatus.OK.name())
            .message("주문 접수가 완료되었습니다.")
            .data(iamportResponse)
            .build();
    }
}
