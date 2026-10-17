package com.cwtask.worker.listener;

import com.cwtask.worker.domain.events.CurrentVersionChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class VersionRegistryListener {

    private static final Logger log = LoggerFactory.getLogger(VersionRegistryListener.class);

    @KafkaListener(topics = "version-registry-topic")
    public void onMessage(CurrentVersionChanged event) {
        log.info("Received CurrentVersionChanged from version-registry-topic: {}", event);
    }

}
