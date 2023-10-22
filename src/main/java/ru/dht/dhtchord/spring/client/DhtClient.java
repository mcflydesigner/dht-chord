package ru.dht.dhtchord.spring.client;

import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;

public interface DhtClient {
    String getDataFromNode(String key, DhtNodeAddress dhtNodeAddress);

    boolean storeDataToNode(String key, String value, DhtNodeAddress dhtNodeAddress);
}
