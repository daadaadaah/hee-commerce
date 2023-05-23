package com.hcommerce.heecommerce.sqs;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SQSConsumer {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(SQSConsumer.class);

    @Autowired
    private SqsTemplate sqsTemplate;

    @SqsListener(value = "${spring.cloud.aws.sqs.nm.output.name}")
    public void receiveMessage(String message) {
        LOGGER.info("received: {}", message);

        if (!StringUtils.hasText(message)) {
            final String errorMessage = "received empty SE Data Ready message";
            LOGGER.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
