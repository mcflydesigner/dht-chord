package ru.dht.dhtchord.spring.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DhtDataTransferRequest {

    @JsonProperty("nodeId")
    private int nodeId;

    @JsonProperty("keys")
    private Set<Integer> keys;

}
