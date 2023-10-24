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
public class DhtInitDataRequest {

    @JsonProperty("data")
    private Map<Integer, Map<String, String>> data;

}
