package ru.dht.dhtchord.core;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public interface DhtNode {
    String getData(String key);

    boolean storeData(String key, String value);

    Set<Integer> getStoredKeys();

    boolean initializeData(Map<Integer, Map<String, String>> data);

    void updateFingerTable(TreeSet<Integer> nodes);

    int getSuccessor();

    int getPredecessor();

    boolean transferDataToNode(int targetNodeId, Set<Integer> requestedKeys);
}
