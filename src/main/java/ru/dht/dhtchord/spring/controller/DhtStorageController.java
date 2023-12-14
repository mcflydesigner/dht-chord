package ru.dht.dhtchord.spring.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.DhtNode;
import ru.dht.dhtchord.core.hash.HashKey;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.spring.client.dto.DhtDataResponse;
import ru.dht.dhtchord.spring.client.dto.DhtStoreRequest;
import ru.dht.dhtchord.spring.client.dto.DhtStoreResponse;

@RestController
@RequestMapping("/storage")
@AllArgsConstructor
public class DhtStorageController {

    private final DhtNode dhtNode;
    private final HashSpace hashSpace;

    @GetMapping
    public DhtDataResponse getDataByKey(@RequestParam String key) {
        HashKey hashKey = hashSpace.fromString(key);
        String data = dhtNode.getData(hashKey);
        DhtNodeMeta node = dhtNode.getNodeMeta();
        return new DhtDataResponse(data, node.getNodeId(), node.getAddress().getAddress());
    }

    @PostMapping
    public DhtStoreResponse storeData(@RequestBody DhtStoreRequest dhtStoreRequest) {
        HashKey hashKey = hashSpace.fromString(dhtStoreRequest.getKey());
        boolean success = dhtNode.storeData(hashKey, dhtStoreRequest.getValue());
        DhtNodeMeta node = dhtNode.getNodeMeta();
        return new DhtStoreResponse(success, node.getNodeId(), node.getAddress().getAddress());
    }
}
