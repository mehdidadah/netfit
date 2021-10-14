package com.mda.client.kafka.listener;

import com.mda.client.kafka.dto.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.VALUE_DESERIALIZER_EXCEPTION_HEADER;

@Component
@Slf4j
public class KafkaMessageListener {


    @KafkaListener(topics = "${kafka.consumer.topic.account}")
    public void listener(KafkaMessage kafkaMessage,
                         @Header(value = VALUE_DESERIALIZER_EXCEPTION_HEADER, required = false) DeserializationException e,
                         ConsumerRecordMetadata metadata) {

        Optional.ofNullable(metadata)
                .ifPresent(m -> Map.of("topic", m.topic(), "partition", "" + m.partition(), "offset", "" + m.offset()).forEach(MDC::put));
        try {

            if (e != null) {
                throw new IllegalArgumentException("Invalid message format", e);
            }

            log.info("Received {}", kafkaMessage);

        } finally {
            List.of("topic", "partition", "offset").forEach(MDC::remove);
        }
    }
}
