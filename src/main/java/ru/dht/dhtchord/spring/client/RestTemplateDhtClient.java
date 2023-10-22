package ru.dht.dhtchord.spring.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.spring.client.dto.DhtDataResponse;
import ru.dht.dhtchord.spring.client.dto.DhtStoreRequest;
import ru.dht.dhtchord.spring.client.dto.DhtStoreResponse;

import java.util.Objects;

@Component
@Slf4j
public class RestTemplateDhtClient implements DhtClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String STORAGE_URI_PATH = "/storage";

    private final static ParameterizedTypeReference<DhtDataResponse> dhtDataResponseTypeRef =
            new ParameterizedTypeReference<DhtDataResponse>() {};
    private final static ParameterizedTypeReference<DhtStoreResponse> dhtStoreResponseTypeRef =
            new ParameterizedTypeReference<DhtStoreResponse>() {};

    @Override
    public String getDataFromNode(String key, DhtNodeAddress dhtNodeAddress) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(dhtNodeAddress.getAddress())
                .path(STORAGE_URI_PATH)
                .query("key={key}")
                .buildAndExpand(key);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<DhtDataResponse> response =
                restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, null, dhtDataResponseTypeRef);
        return Objects.requireNonNull(response.getBody()).getData();
    }

    @Override
    public boolean storeDataToNode(String key, String value, DhtNodeAddress dhtNodeAddress) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(dhtNodeAddress.getAddress())
                .path(STORAGE_URI_PATH)
                .buildAndExpand();

        HttpEntity<DhtStoreRequest> entity = new HttpEntity<>(new DhtStoreRequest(key, value));
        RestTemplate restTemplate = new RestTemplate();

        DhtStoreResponse response =
                restTemplate.postForObject(uriComponents.toUriString(), entity, DhtStoreResponse.class);
        return response.getSuccess();
    }

}
