package ru.dht.dhtchord.spring.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.dht.dhtchord.core.DhtNode;
import ru.dht.dhtchord.core.hash.HashKey;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.spring.client.dto.*;

@RestController
@RequestMapping("/storage")
@AllArgsConstructor
public class DhtStorageController {

    private final DhtNode dhtNode;

    @GetMapping
    public DhtDataResponse getDataByKey(@RequestParam String key) {
        HashKey hashKey = HashKey.fromString(key);
        String data = dhtNode.getData(hashKey);
        return new DhtDataResponse(data);
    }

    @PostMapping
    public DhtStoreResponse storeData(@RequestBody DhtStoreRequest dhtStoreRequest) {
        HashKey hashKey = HashKey.fromString(dhtStoreRequest.getKey());
        boolean success = dhtNode.storeData(hashKey, dhtStoreRequest.getValue());
        return new DhtStoreResponse(success);
    }

//    @PostMapping("/initialize")
//    public DhtInitDataResponse initializeStorageData(@RequestBody DhtInitDataRequest dhtInitDataRequest) {
//        boolean success = dhtNode.initializeData(dhtInitDataRequest.getData());
//        return new DhtInitDataResponse(success);
//    }

}
