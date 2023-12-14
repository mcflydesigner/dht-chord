package ru.dht.dhtchord.spring.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.spring.client.dto.*;

import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@Component
@Slf4j
public class RestTemplateDhtClient implements DhtClient {

    private final HashSpace hashSpace;
    private final RestTemplate restTemplate;

    private static final String STORAGE_URI_PATH = "/storage";

    private static final String NOTIFY_ABOUT_PREDECESSOR = "/topology/predecessor/notify";
    private static final String GET_PREDECESSOR_URI_PATH = "/topology/predecessor";
    private static final String SET_PREDECESSOR_URI_PATH = "/topology/predecessor";
    private static final String FIND_SUCCESSOR_URI_PATH = "/topology/successor";
    private static final String JOIN_URI_PATH = "/topology/join";
    private static final String JOIN_CONFIRM_URI_PATH = "/topology/join/confirm";

    private final static ParameterizedTypeReference<DhtDataResponse> dhtDataResponseTypeRef =
            new ParameterizedTypeReference<DhtDataResponse>() {};
    private final static ParameterizedTypeReference<DhtNodeMetaDto> dhtNodeMetaDtoRef =
            new ParameterizedTypeReference<DhtNodeMetaDto>() {};
    private final static ParameterizedTypeReference<DhtNodeJoinResponse> dhtNodeJoinResponseTypeRef =
            new ParameterizedTypeReference<DhtNodeJoinResponse>() {};

    @Override
    public String getDataFromNode(String key, DhtNodeAddress dhtNodeAddress) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(dhtNodeAddress.getAddress())
                .path(STORAGE_URI_PATH)
                .query("key={key}")
                .buildAndExpand(key);

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

        DhtStoreResponse response =
                restTemplate.postForObject(uriComponents.toUriString(), entity, DhtStoreResponse.class);
        return response.getSuccess();
    }

    @Override
    public DhtNodeMeta findSuccessor(String key, DhtNodeAddress dhtNodeAddress) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(dhtNodeAddress.getAddress())
                .path(FIND_SUCCESSOR_URI_PATH)
                .query("key={key}")
                .buildAndExpand(key);

        ResponseEntity<DhtNodeMetaDto> response =
                restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, null, dhtNodeMetaDtoRef);
        DhtNodeMetaDto nodeMetaDto = response.getBody();
        return new DhtNodeMeta(nodeMetaDto.getNodeId(),
                hashSpace.fromString(nodeMetaDto.getKey()),
                new DhtNodeAddress(nodeMetaDto.getAddress())
        );
    }

    @Override
    public DhtNodeMeta updatePredecessor(DhtNodeMeta predecessor, DhtNodeAddress dhtNodeAddress) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(dhtNodeAddress.getAddress())
                .path(SET_PREDECESSOR_URI_PATH)
                .buildAndExpand();

        HttpEntity<DhtNodeMetaDto> entity = new HttpEntity<>(
                new DhtNodeMetaDto(predecessor.getNodeId(), predecessor.getKey().toString(), predecessor.getAddress().getAddress())
        );
        ResponseEntity<DhtNodeMetaDto> response =
                restTemplate.exchange(uriComponents.toUriString(), HttpMethod.PUT, entity, dhtNodeMetaDtoRef);
        DhtNodeMetaDto predecessorDto = response.getBody();

        return new DhtNodeMeta(predecessorDto.getNodeId(),
                hashSpace.fromString(predecessorDto.getKey()),
                new DhtNodeAddress(predecessorDto.getAddress())
        );
    }

    @Override
    public DhtNodeMeta getPredecessor(DhtNodeAddress dhtNodeAddress) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(dhtNodeAddress.getAddress())
                .path(GET_PREDECESSOR_URI_PATH)
                .buildAndExpand();

        ResponseEntity<DhtNodeMetaDto> response =
                restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, null, dhtNodeMetaDtoRef);
        DhtNodeMetaDto predecessorDto = response.getBody();

        return new DhtNodeMeta(predecessorDto.getNodeId(),
                hashSpace.fromString(predecessorDto.getKey()),
                new DhtNodeAddress(predecessorDto.getAddress())
        );
    }

    @Override
    public void notifyAboutPredecessor(DhtNodeMeta predecessor, DhtNodeAddress node) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(node.getAddress())
                .path(NOTIFY_ABOUT_PREDECESSOR)
                .buildAndExpand();

        HttpEntity<DhtNodeMetaDto> entity = new HttpEntity<>(
                new DhtNodeMetaDto(predecessor.getNodeId(), predecessor.getKey().toString(), predecessor.getAddress().getAddress())
        );

        restTemplate.postForObject(uriComponents.toUriString(), entity, Object.class);
    }

    @Override
    public Map<String, String> getDataToTransfer(DhtNodeMeta dhtNodeMeta, DhtNodeAddress dhtNodeAddress) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(dhtNodeAddress.getAddress())
                .path(JOIN_URI_PATH)
                .query("nodeId={nodeId}&key={key}&address={address}")
                .buildAndExpand(dhtNodeMeta.getNodeId(), dhtNodeMeta.getKey().toString(), dhtNodeMeta.getAddress().getAddress());

        ResponseEntity<DhtNodeJoinResponse> response = restTemplate.exchange(uriComponents.toUriString(),
                HttpMethod.GET, null, dhtNodeJoinResponseTypeRef);
        return response.getBody().getData();
    }

    @Override
    public boolean confirmDataTransfer(DhtNodeMeta dhtNodeMeta, DhtNodeAddress dhtNodeAddress) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(dhtNodeAddress.getAddress())
                .path(JOIN_CONFIRM_URI_PATH)
                .buildAndExpand();

        HttpEntity<DhtNodeJoinRequest> entity = new HttpEntity<>(
                new DhtNodeJoinRequest(
                        dhtNodeMeta.getNodeId(), dhtNodeMeta.getKey().toString(), dhtNodeMeta.getAddress().getAddress()
                )
        );
        DhtNodeJoinConfirmResponse response =
                restTemplate.postForObject(uriComponents.toUriString(), entity, DhtNodeJoinConfirmResponse.class);
        return response.isSuccess();
    }

}
