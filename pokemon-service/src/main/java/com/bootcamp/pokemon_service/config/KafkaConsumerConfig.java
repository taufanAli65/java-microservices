package com.bootcamp.pokemon_service.config;
import com.bootcamp.pokemon_service.dto.message.ProductViewMessage;
import com.bootcamp.pokemon_service.dto.message.TradeCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        return consumerFactory(Object.class);
    }

    @Bean
    public ConsumerFactory<String, ProductViewMessage> productViewConsumerFactory() {
        return consumerFactory(ProductViewMessage.class);
    }

    @Bean
    public ConsumerFactory<String, TradeCreatedMessage> tradeCreatedConsumerFactory() {
        return consumerFactory(TradeCreatedMessage.class);
    }

    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> targetType) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, targetType.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductViewMessage> productViewMessageConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, ProductViewMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productViewConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TradeCreatedMessage> tradeCreatedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TradeCreatedMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tradeCreatedConsumerFactory());

        return factory;
    }
}
