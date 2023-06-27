package com.hcommerce.heecommerce.common.configuration;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamportConfig {
    @Value("${payment.iamport.rest-api-key}")
    private String iamportRestApiKey;

    @Value("${payment.iamport.rest-api-secret}")
    private String iamportRestApiSecret;

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(iamportRestApiKey, iamportRestApiSecret);
    }
}
