package ru.dht.dhtchord.spring.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DhtNodeMetaDto {

    @JsonProperty("nodeId")
    private String nodeId;
    @JsonProperty("key")
    private String key;
    @JsonProperty("address")
    private String address;

}
