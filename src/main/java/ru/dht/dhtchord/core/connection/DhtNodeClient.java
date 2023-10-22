package ru.dht.dhtchord.core.connection;

import lombok.AllArgsConstructor;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.spring.client.DhtClient;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@AllArgsConstructor
public class DhtNodeClient {

    private final DhtClient dhtClient;
    private final AtomicReference<Map<Integer, DhtNodeAddress>> nodeAddressesMapRef;

    public String getDataFromNode(int nodeId, String key) {
        return dhtClient.getDataFromNode(key, getDhtNodeAddress(nodeId));
    }

    public boolean storeDataToNode(int nodeId, String key, String value) {
        return dhtClient.storeDataToNode(key, value, getDhtNodeAddress(nodeId));
    }

    private DhtNodeAddress getDhtNodeAddress(int nodeId) {
        Map<Integer, DhtNodeAddress> nodeAddressesMap = nodeAddressesMapRef.get();
        if (!nodeAddressesMap.containsKey(nodeId)) {
            throw new IllegalStateException(String.format("Cannot find node with nodeId = %d", nodeId));
        }
        return nodeAddressesMap.get(nodeId);
    }

}
