package ru.dht.dhtchord.core;

import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.hash.HashKey;

public interface DhtNode {
    String getData(HashKey key);

    boolean storeData(HashKey key, String value);

    void stabilize();

    DhtNodeMeta findSuccessor(HashKey key);

    DhtNodeMeta updatePredecessor(DhtNodeMeta dhtNodeMeta);

    DhtNodeMeta getPredecessor();

    DhtNodeMeta getSuccessor();

    DhtNodeMeta getNodeMeta();

    void notifyAboutPredecessor(DhtNodeMeta dhtNodeMeta);

    void fixFinger();
}
