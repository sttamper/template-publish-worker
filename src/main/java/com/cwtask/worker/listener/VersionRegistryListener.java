package com.cwtask.worker.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class VersionRegistryListener {

    private static final Logger log = LoggerFactory.getLogger(VersionRegistryListener.class);

    @KafkaListener(topics = "version-registry-topic")
    public void onMessage(String message) {
        log.info("Received message from version-registry-topic: {}", message);
    }

}
