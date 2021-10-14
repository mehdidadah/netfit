package com.mda.client.kafka.config;

import com.mda.client.kafka.dto.KafkaMessage;
import com.mda.client.kafka.mapper.AvroDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.io.IOException;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    /**
     * Accept both JSON and Avro formats in local environment.
     * <ul>
     *     <li>Keep the message-producing Makefile targets working, as JSON is easier to handle in pure shell;</li>
     *     <li>Shall be removed as soon as the dev tooling is able to produce custom Avro messages.</li>
     * </ul>
     *
     * @see #consumerFactory(KafkaProperties, Resource)
     */
    @Bean
    public ConsumerFactory<?, ?> consumerFactory(KafkaProperties kafkaProperties,
                                                 @Value("${kafka.consumer.schema.EMP}") Resource schemaResource) throws IOException {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(), new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new AvroDeserializer<>(KafkaMessage.class, schemaResource)));
    }

    @Bean
    public ErrorHandler errorHandler(
            @Value("${kafka.consumer.backoff.initialIntervalMs}") long initialIntervalMs,
            @Value("${kafka.consumer.backoff.multiplier}") double multiplier,
            @Value("${kafka.consumer.backoff.maxIntervalMs}") long maxIntervalMs,
            @Value("${kafka.consumer.backoff.maxElapsedTimeMs}") long maxElapsedTimeMs) {
        ExponentialBackOff backOff = new ExponentialBackOff(initialIntervalMs, multiplier);
        backOff.setMaxInterval(maxIntervalMs);
        backOff.setMaxElapsedTime(maxElapsedTimeMs);
        return new SeekToCurrentErrorHandler(backOff);
    }
}
