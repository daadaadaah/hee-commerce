package com.hcommerce.heecommerce.sqs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SQSController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(SQSController.class);
    @Autowired
    private SQSProducer SQSProducer;

    @PostMapping("/sendMessage")
    public void sendMessage(@RequestBody String message) {
        SQSProducer.sendMessage(message);
    }
}
