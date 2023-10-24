package ru.dht.dhtchord.spring.client;

import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;

import java.util.Map;

public interface DhtClient {
    String getDataFromNode(String key, DhtNodeAddress dhtNodeAddress);

    boolean storeDataToNode(String key, String value, DhtNodeAddress dhtNodeAddress);

    boolean registerNewNode(DhtNodeMeta dhtNodeMeta, DhtNodeAddress dhtNodeAddress);

    boolean transferDataToNode(Map<Integer, Map<String, String>> data, DhtNodeAddress dhtNodeAddress);
}
