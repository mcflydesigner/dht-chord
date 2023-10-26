package ru.dht.dhtchord.core.connection;

import lombok.AllArgsConstructor;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.hash.HashKey;
import ru.dht.dhtchord.core.storage.KeyValueStorage;
import ru.dht.dhtchord.spring.client.DhtClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@AllArgsConstructor
// TODO: add interface
public class DhtNodeClient {

    private final DhtClient dhtClient;
    private final AtomicReference<Map<Integer, DhtNodeAddress>> nodeAddressesMapRef;

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

//    public boolean registerNewNode(int nodeId, DhtNodeMeta dhtNodeMeta) {
//        return dhtClient.registerNewNode(dhtNodeMeta, getDhtNodeAddress(nodeId));
//    }
//
//    public boolean requestTransferDataToNode(int fromNodeId, int toNodeId, Set<Integer> keys) {
//        return dhtClient.requestTransferDataToNode(fromNodeId, toNodeId, keys, getDhtNodeAddress(fromNodeId));
//    }
//
//    public boolean transferDataToNode(int nodeId, Map<Integer, Map<String, String>> data) {
//        return dhtClient.transferDataToNode(data, getDhtNodeAddress(nodeId));
//    }
//
//    private DhtNodeAddress getDhtNodeAddress(int nodeId) {
//        Map<Integer, DhtNodeAddress> nodeAddressesMap = nodeAddressesMapRef.get();
//        if (!nodeAddressesMap.containsKey(nodeId)) {
//            throw new IllegalStateException(String.format("Cannot find node with nodeId = %d", nodeId));
//        }
//        return nodeAddressesMap.get(nodeId);
//    }

}
