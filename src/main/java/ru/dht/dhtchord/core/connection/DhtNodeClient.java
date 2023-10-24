package ru.dht.dhtchord.core.connection;

import lombok.AllArgsConstructor;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.storage.KeyValueStorage;
import ru.dht.dhtchord.spring.client.DhtClient;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@AllArgsConstructor
// TODO: add interface
public class DhtNodeClient {

    private final DhtClient dhtClient;
    private final AtomicReference<Map<Integer, DhtNodeAddress>> nodeAddressesMapRef;

    public String getDataFromNode(int nodeId, String key) {
        return dhtClient.getDataFromNode(key, getDhtNodeAddress(nodeId));
    }

    public boolean storeDataToNode(int nodeId, String key, String value) {
        return dhtClient.storeDataToNode(key, value, getDhtNodeAddress(nodeId));
    }

    public boolean registerNewNode(int nodeId, DhtNodeMeta dhtNodeMeta) {
        return dhtClient.registerNewNode(dhtNodeMeta, getDhtNodeAddress(nodeId));
    }

    public boolean transferDataToNode(int nodeId, Map<Integer, KeyValueStorage> data) {
        Map<Integer, Map<String, String>> map = new HashMap<>();
        data.forEach((currentNodeId, value) -> map.put(currentNodeId, value.getKeys().stream().collect(Collectors.toMap(
                        key -> key,
                        value::getData
                ))
        ));

        return dhtClient.transferDataToNode(map, getDhtNodeAddress(nodeId));
    }

    private DhtNodeAddress getDhtNodeAddress(int nodeId) {
        Map<Integer, DhtNodeAddress> nodeAddressesMap = nodeAddressesMapRef.get();
        if (!nodeAddressesMap.containsKey(nodeId)) {
            throw new IllegalStateException(String.format("Cannot find node with nodeId = %d", nodeId));
        }
        return nodeAddressesMap.get(nodeId);
    }

}
