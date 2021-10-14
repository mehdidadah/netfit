package com.mda.client.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.avro.reflect.AvroName;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KafkaMessage implements Serializable {

    private static final long serialVersionUID = 0L;

    @JsonProperty("name")
    @AvroName("name")
    private String name;

    @JsonProperty("age")
    @AvroName("age")
    private Integer age;
}
