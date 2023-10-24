package ru.dht.dhtchord.spring.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.core.DhtChordRing;
import ru.dht.dhtchord.spring.client.dto.DhtDataTransferRequest;
import ru.dht.dhtchord.spring.client.dto.DhtNodeRegisterRequest;

@AllArgsConstructor
@RestController
@RequestMapping("/topology")
public class DhtTopologyController {

    private final DhtChordRing dhtChordRing;

    @PostMapping("/register")
    public boolean registerNewNode(@RequestBody DhtNodeRegisterRequest nodeJoinRequest) {
        return dhtChordRing.addNode(nodeJoinRequest.getNodeId(), new DhtNodeAddress(nodeJoinRequest.getAddress()));
    }

    @PostMapping("/data/transfer")
    public boolean requestDataTransferToNode(@RequestBody DhtDataTransferRequest dhtDataTransferRequest) {
        return dhtChordRing.requestToTransferDataToNode(dhtDataTransferRequest.getNodeId(),
                dhtDataTransferRequest.getKeys());
    }

}
