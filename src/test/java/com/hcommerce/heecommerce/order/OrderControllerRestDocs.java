package com.hcommerce.heecommerce.order;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

public class OrderControllerRestDocs {
    public static RestDocumentationResultHandler completeOrderReceipt() {
        return document("order-receipt-complete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("orderUuid").description("주문 ID")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
                )
        );
    }

    public static RestDocumentationResultHandler placeOrder() {
        return document("place-order",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                // 주문자 정보
                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("주문자 USER ID"),

                // 받는 사람 정보
                fieldWithPath("recipientInfoForm.recipientName").type(JsonFieldType.STRING).description("받는 사람 이름"),
                fieldWithPath("recipientInfoForm.recipientPhoneNumber").type(JsonFieldType.STRING).description("받는 사람 휴대폰 번호"),
                fieldWithPath("recipientInfoForm.recipientAddress").type(JsonFieldType.STRING).description("받는 사람 주소"),
                fieldWithPath("recipientInfoForm.recipientDetailAddress").type(JsonFieldType.STRING).optional().description("받는 사람 상세 주소"),
                fieldWithPath("recipientInfoForm.shippingRequest").type(JsonFieldType.STRING).optional().description("배송 요청 사항"),

                // 결제 정보
                fieldWithPath("dealProductUuid").type(JsonFieldType.STRING).description("딜 상품 UUID"),
                fieldWithPath("orderQuantity").type(JsonFieldType.NUMBER).description("주문 수량"),
                fieldWithPath("paymentType").type(JsonFieldType.STRING).description("결제 방법")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
            )
        );
    }
}
