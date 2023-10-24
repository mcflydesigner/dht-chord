package ru.dht.dhtchord.spring.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.dht.dhtchord.core.DhtNode;
import ru.dht.dhtchord.spring.client.dto.*;

@RestController
@RequestMapping("/storage")
@AllArgsConstructor
public class DhtStorageController {

    private final DhtNode dhtNode;

    @GetMapping
    public DhtDataResponse getDataByKey(@RequestParam String key) {
        String data = dhtNode.getData(key);
        return new DhtDataResponse(data);
    }

    @PostMapping
    public DhtStoreResponse storeData(@RequestBody DhtStoreRequest dhtStoreRequest) {
        Boolean success = dhtNode.storeData(dhtStoreRequest.getKey(), dhtStoreRequest.getValue());
        return new DhtStoreResponse(success);
    }

    @PostMapping("/initialize")
    public DhtInitDataResponse initializeStorageData(@RequestBody DhtInitDataRequest dhtInitDataRequest) {
        Boolean success = dhtNode.initializeData(dhtInitDataRequest.getData());
        return new DhtInitDataResponse(success);
    }

}
