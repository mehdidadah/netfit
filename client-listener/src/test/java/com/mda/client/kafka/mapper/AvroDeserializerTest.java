package com.mda.client.kafka.mapper;

import com.mda.client.kafka.dto.KafkaMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AvroDeserializerTest {

    private static final String AVRO_SCHEMA_PATH = "/avro/CRECPT-value.avsc";

    private AvroDeserializer<KafkaMessage> deserializer;

    void deserialize() throws Exception {
        // GIVEN
        byte[] sampleData = readAndDecodeData(messageResource);

        // WHEN
        deserializer = new AvroDeserializer<>(KafkaMessage.class, new ClassPathResource(AVRO_SCHEMA_PATH));
        var result = deserializer.deserialize("Test", sampleData);

        // THEN
        assertThat(result).isEqualTo(expected);
    }

    private static byte[] readAndDecodeData(String classpathResource) throws IOException {
        try (InputStream in = new ClassPathResource(classpathResource).getInputStream()) {
            String content = FileCopyUtils.copyToString(new InputStreamReader(in));
            return Base64.getDecoder().decode(content.trim());
        }
    }
}