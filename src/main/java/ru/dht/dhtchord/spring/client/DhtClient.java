package ru.dht.dhtchord.spring.client;

import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.hash.HashKey;

import java.util.Map;

public interface DhtClient {
    String getDataFromNode(String key, DhtNodeAddress dhtNodeAddress);

    boolean storeDataToNode(String key, String value, DhtNodeAddress dhtNodeAddress);

    DhtNodeMeta findSuccessor(String key, DhtNodeAddress dhtNodeAddress);

    DhtNodeMeta updatePredecessor(DhtNodeMeta predecessor, DhtNodeAddress dhtNodeAddress);

    DhtNodeMeta getPredecessor(DhtNodeAddress dhtNodeAddress);

    void notifyAboutPredecessor(DhtNodeMeta predecessor, DhtNodeAddress dhtNodeAddress);

    Map<String, String> getDataToTransfer(DhtNodeMeta node, DhtNodeAddress dhtNodeAddress);

    boolean confirmDataTransfer(DhtNodeMeta node, DhtNodeAddress dhtNodeAddress);
}
