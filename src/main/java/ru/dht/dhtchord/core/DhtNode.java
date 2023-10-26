package ru.dht.dhtchord.core;

import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.hash.HashKey;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public interface DhtNode {
    String getData(HashKey key);

    boolean storeData(HashKey key, String value);

//    void updateFingerTable(DhtNodeMeta newNode);

    DhtNodeMeta findSuccessor(HashKey key);

    DhtNodeMeta updatePredecessor(DhtNodeMeta dhtNodeMeta);
//
//    int getPredecessor();
//
//    boolean transferDataToNode(int targetNodeId, Set<Integer> requestedKeys);
}
