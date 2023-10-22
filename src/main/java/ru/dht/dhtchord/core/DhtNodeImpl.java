package ru.dht.dhtchord.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.dht.dhtchord.core.connection.DhtNodeClient;
import ru.dht.dhtchord.core.storage.KeyValueInMemoryStorage;
import ru.dht.dhtchord.core.storage.KeyValueStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Slf4j
public class DhtNodeImpl implements DhtNode {

    private final int m;
    private final int nodeId;
    private final Set<Integer> storedKeys;
    private final KeyValueStorage keyValueStorage = new KeyValueInMemoryStorage();
    private final AtomicReference<FingerTable> fingerTableRef;
    private final DhtNodeClient dhtNodeClient;

    public static DhtNode build(int m,
                                int nodeId,
                                TreeSet<Integer> nodes,
                                AtomicReference<FingerTable> fingerTableRef,
                                DhtNodeClient dhtNodeClient) {
        Utils.verifyNodesSetIsNotEmpty(nodes);

        Set<Integer> storedKeys = new HashSet<>();
        int predecessorForNodeId = nodes.first();
        for (Integer currentNodeId : nodes) {
            if (currentNodeId >= nodeId) {
                break;
            }
            if (predecessorForNodeId < currentNodeId) {
                predecessorForNodeId = currentNodeId;
            }
        }
        // Adding the node IDs in the range: (predecessor; nodeId]
        for (int i = predecessorForNodeId + 1; i < nodeId; i++) {
            storedKeys.add(nodeId);
        }
        storedKeys.add(nodeId);

        return new DhtNodeImpl(m, nodeId, storedKeys, fingerTableRef, dhtNodeClient);
    }

    public String getData(String key) {
        int requiredNode = HashUtils.getNodeIdForKey(key, m);
        if (storedKeys.contains(requiredNode)) {
            log.info("Getting data from the current node (nodeId = {}) cache by the key {}", nodeId, key);
            return keyValueStorage.getData(key);
        }
        return redirectGetRequest(key);
    }

    public boolean storeData(String key, String value) {
        int requiredNode = HashUtils.getNodeIdForKey(key, m);
        if (storedKeys.contains(requiredNode)) {
            log.info("Saving key `{}` and value `{}` on the current node (nodeId = {})", nodeId, key, value);
            return keyValueStorage.storeData(key, value);
        }
        return redirectStoreRequest(key, value);
    }

    private boolean redirectStoreRequest(String key, String value) {
        int successorForKey = fingerTableRef.get().lookupSuccessorForKey(key);
        log.info("Key `{}` and value `{}` will not be put on the current node (nodeId = {}), redirecting request to the successor node (nodeId = {})",
                key, value, nodeId, successorForKey);
        return dhtNodeClient.storeDataToNode(successorForKey, key, value);
    }

    private String redirectGetRequest(String key) {
        int successorForKey = fingerTableRef.get().lookupSuccessorForKey(key);
        log.info("Key `{}` is not located on the current node (nodeId = {}), redirecting request to the successor node (nodeId = {})",
                key, nodeId, successorForKey);
        return dhtNodeClient.getDataFromNode(successorForKey, key);
    }

}
