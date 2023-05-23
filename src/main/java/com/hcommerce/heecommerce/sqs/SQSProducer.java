package com.hcommerce.heecommerce.sqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SQSProducer {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(SQSProducer.class);

    @Value("${spring.cloud.aws.sqs.pr.input.name}")
    private String prQueueName;

    @Autowired
    private SqsTemplate sqsTemplate;

    public void sendMessage(String message) {
        if (message == null) {
            final String errorMessage = "empty message";
            LOGGER.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        sqsTemplate.send(prQueueName, message);

        LOGGER.info("PR message: {} sent to queue: {}", message, prQueueName);
    }
}
