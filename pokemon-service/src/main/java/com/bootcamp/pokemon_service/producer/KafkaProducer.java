package com.bootcamp.pokemon_service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer<T> {
    private final KafkaTemplate<String, T> kafkaTemplate;

    public void sendMesage(String topic, T message) {
        log.info("Sending to topic -> {} | Payload -> {}", topic, message);
        kafkaTemplate.send(topic, message);
    }
}
