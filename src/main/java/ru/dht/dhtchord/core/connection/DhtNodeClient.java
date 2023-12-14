package ru.dht.dhtchord.core.connection;

import lombok.AllArgsConstructor;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.hash.HashKey;
import ru.dht.dhtchord.spring.client.DhtClient;

import java.util.Map;

@AllArgsConstructor
public class DhtNodeClient {

    private final DhtClient dhtClient;

    public String getDataFromNode(DhtNodeMeta node, HashKey key) {
        return dhtClient.getDataFromNode(key.toString(), node.getAddress());
    }

    public boolean storeDataToNode(DhtNodeMeta node, HashKey key, String value) {
        return dhtClient.storeDataToNode(key.toString(), value, node.getAddress());
    }

    public DhtNodeMeta findSuccessor(DhtNodeMeta node, HashKey key) {
        return dhtClient.findSuccessor(key.toString(), node.getAddress());
    }

    public DhtNodeMeta updatePredecessor(DhtNodeMeta node, DhtNodeMeta predecessor) {
        return dhtClient.updatePredecessor(predecessor, node.getAddress());
    }

    public DhtNodeMeta getPredecessor(DhtNodeMeta node) {
        return dhtClient.getPredecessor(node.getAddress());
    }

    public void notifyAboutPredecessor(DhtNodeMeta predecessor, DhtNodeMeta node) {
        dhtClient.notifyAboutPredecessor(predecessor, node.getAddress());
    }

    public Map<String, String> getDataToTransfer(DhtNodeMeta successor, DhtNodeMeta node) {
        return dhtClient.getDataToTransfer(node, successor.getAddress());
    }

    public boolean confirmDataTransfer(DhtNodeMeta successor, DhtNodeMeta node) {
        return dhtClient.confirmDataTransfer(node, successor.getAddress());
    }
}
