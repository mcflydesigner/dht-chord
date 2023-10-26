package ru.dht.dhtchord.spring.client;

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
import ru.dht.dhtchord.spring.client.dto.*;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class RestTemplateDhtClient implements DhtClient {

    private static final String STORAGE_URI_PATH = "/storage";
    private static final String STORAGE_INIT_URI_PATH = STORAGE_URI_PATH + "/initialize";

    private static final String REGISTER_NODE_URI_PATH = "/topology/register";
    private static final String REQUEST_DATA_TRANSFER_URI_PATH = "/topology/data/transfer";

    private final static ParameterizedTypeReference<DhtDataResponse> dhtDataResponseTypeRef =
            new ParameterizedTypeReference<DhtDataResponse>() {};

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

//    @Override
//    public boolean registerNewNode(DhtNodeMeta dhtNodeMeta, DhtNodeAddress dhtNodeAddress) {
//        UriComponents uriComponents = UriComponentsBuilder.newInstance()
//                .scheme("http")
//                .host(dhtNodeAddress.getAddress())
//                .path(REGISTER_NODE_URI_PATH)
//                .buildAndExpand();
//
//        HttpEntity<DhtNodeRegisterRequest> entity = new HttpEntity<>(new DhtNodeRegisterRequest(
//                dhtNodeMeta.getNodeId(), dhtNodeMeta.getAddress().getAddress()
//        ));
//        RestTemplate restTemplate = new RestTemplate();
//        DhtNodeRegisterResponse response =
//                restTemplate.postForObject(uriComponents.toUriString(), entity, DhtNodeRegisterResponse.class);
//        return response.getSuccess();
//    }
//
//    @Override
//    public boolean transferDataToNode(Map<Integer, Map<String, String>> data, DhtNodeAddress dhtNodeAddress) {
//        UriComponents uriComponents = UriComponentsBuilder.newInstance()
//                .scheme("http")
//                .host(dhtNodeAddress.getAddress())
//                .path(STORAGE_INIT_URI_PATH)
//                .buildAndExpand();
//
//        HttpEntity<DhtInitDataRequest> entity = new HttpEntity<>(new DhtInitDataRequest(data));
//        RestTemplate restTemplate = new RestTemplate();
//
//        DhtInitDataResponse response =
//                restTemplate.postForObject(uriComponents.toUriString(), entity, DhtInitDataResponse.class);
//        return response.getSuccess();
//    }
//
//    @Override
//    public boolean requestTransferDataToNode(int fromNodeId,
//                                             int toNodeId,
//                                             Set<Integer> keys,
//                                             DhtNodeAddress dhtNodeAddress) {
//        UriComponents uriComponents = UriComponentsBuilder.newInstance()
//                .scheme("http")
//                .host(dhtNodeAddress.getAddress())
//                .path(REQUEST_DATA_TRANSFER_URI_PATH)
//                .buildAndExpand();
//
//        HttpEntity<DhtDataTransferRequest> entity = new HttpEntity<>(
//                new DhtDataTransferRequest(fromNodeId, toNodeId, keys)
//        );
//        RestTemplate restTemplate = new RestTemplate();
//
//        DhtDataTransferResponse response =
//                restTemplate.postForObject(uriComponents.toUriString(), entity, DhtDataTransferResponse.class);
//        return response.getSuccess();
//    }
}
