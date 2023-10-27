package ru.dht.dhtchord.spring.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.DhtNode;
import ru.dht.dhtchord.core.hash.HashKey;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.spring.client.dto.DhtNodeMetaDto;

@AllArgsConstructor
@RestController
@RequestMapping("/topology")
@Slf4j
public class DhtTopologyController {

    private HashSpace hashSpace;
    private DhtNode dhtNode;

    @GetMapping("/successor")
    public DhtNodeMetaDto findSuccessor(@RequestParam(required = false) String key) {
        DhtNodeMeta successor;
        if (key != null) {
            HashKey hashKey = hashSpace.fromString(key);
            successor = dhtNode.findSuccessor(hashKey);
        } else {
            successor = dhtNode.getSuccessor();
        }
        return new DhtNodeMetaDto(successor.getNodeId(), successor.getKey().toString(), successor.getAddress().getAddress());
    }

    @GetMapping("/predecessor")
    public DhtNodeMetaDto getPredecessor() {
        DhtNodeMeta predecessor = dhtNode.getPredecessor();
        return new DhtNodeMetaDto(predecessor.getNodeId(), predecessor.getKey().toString(), predecessor.getAddress().getAddress());
    }

    @PutMapping("/predecessor")
    public DhtNodeMetaDto updatePredecessorAndReturnOld(@RequestBody DhtNodeMetaDto predecessorDto) {
        HashKey hashKey = hashSpace.fromString(predecessorDto.getKey());
        DhtNodeMeta oldPredecessor = dhtNode.updatePredecessor(new DhtNodeMeta(predecessorDto.getNodeId(), hashKey, new DhtNodeAddress(predecessorDto.getAddress())));
        return new DhtNodeMetaDto(oldPredecessor.getNodeId(), oldPredecessor.getKey().toString(), oldPredecessor.getAddress().getAddress());
    }

    @PostMapping("/predecessor/notify")
    public void notifyAboutPredecessor(@RequestBody DhtNodeMetaDto dhtNodeMetaDto) {
        HashKey hashKey = hashSpace.fromString(dhtNodeMetaDto.getKey());
        DhtNodeMeta dhtNodeMeta = new DhtNodeMeta(dhtNodeMetaDto.getNodeId(), hashKey, new DhtNodeAddress(dhtNodeMetaDto.getAddress()));
        dhtNode.notifyAboutPredecessor(dhtNodeMeta);
    }

}
