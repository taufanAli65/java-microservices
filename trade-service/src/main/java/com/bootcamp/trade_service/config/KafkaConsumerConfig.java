package com.bootcamp.trade_service.config;

import com.bootcamp.trade_service.dto.message.TradeResultMessage;
import com.bootcamp.trade_service.dto.message.TradeStatusRetryMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Bean
    public ConsumerFactory<String, TradeResultMessage> tradeResultConsumerFactory() {
        return consumerFactory(TradeResultMessage.class, "trade-service-result");
    }

    @Bean
    public ConsumerFactory<String, TradeStatusRetryMessage> tradeRetryConsumerFactory() {
        return consumerFactory(TradeStatusRetryMessage.class, "trade-service-retry");
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TradeResultMessage> tradeResultKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TradeResultMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tradeResultConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TradeStatusRetryMessage> tradeRetryKafkaListenerContainerFactory(
            DefaultErrorHandler tradeRetryErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, TradeStatusRetryMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tradeRetryConsumerFactory());
        factory.setCommonErrorHandler(tradeRetryErrorHandler);
        return factory;
    }

    @Bean
    public DefaultErrorHandler tradeRetryErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> new TopicPartition(record.topic() + ".DLT", record.partition())
        );

        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));
    }

    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> targetType, String groupId) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(targetType);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        deserializer.setUseTypeMapperForKey(false);
        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }
}
