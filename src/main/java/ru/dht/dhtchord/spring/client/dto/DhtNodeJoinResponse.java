package ru.dht.dhtchord.spring.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DhtNodeJoinResponse {
    @JsonProperty("data")
    private Map<String, String> data;
    @JsonProperty("nodeId")
    private String nodeId;
    @JsonProperty("address")
    private String address;
}
