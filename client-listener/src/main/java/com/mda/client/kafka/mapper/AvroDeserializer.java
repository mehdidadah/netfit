package com.mda.client.kafka.mapper;

import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AvroDeserializer<M> implements Deserializer<M> {
    /**
     * First bytes in messages to skip when deserializing records, conforming to Confluent standard.
     * <ul>
     *     <li>A 1-byte magic number ({@literal 0x0})</li>
     *     <li>A 4-bytes schema ID ({@code int})</li>
     * </ul>
     */
    public static final int MESSAGE_PREFIX_LENGTH = 5;

    private final Class<M> messageType;
    private final Schema schema;

    public AvroDeserializer(Class<M> messageType, Resource schemaResource) throws IOException {
        this.messageType = messageType;

        String jsonSchema;
        try (var in = schemaResource.getInputStream()) {
            jsonSchema = StreamUtils.copyToString(in, UTF_8);
        }
        this.schema = new Schema.Parser().parse(jsonSchema);
    }

    @Override
    public void configure(Map<String, ?> map, boolean b) {}

    @Override
    public M deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, MESSAGE_PREFIX_LENGTH, data.length - MESSAGE_PREFIX_LENGTH, null);

            ReflectDatumReader<M> datumReader = new ReflectDatumReader<>(messageType);
            datumReader.setSchema(schema);

            return datumReader.read(null, decoder);

        } catch (Exception e) {
            throw new SerializationException("Can't deserialize data from topic [" + topic + "]", e);
        }
    }

    @Override
    public void close() {}
}
