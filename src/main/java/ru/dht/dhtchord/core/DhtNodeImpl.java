package ru.dht.dhtchord.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.dht.dhtchord.core.connection.DhtNodeClient;
import ru.dht.dhtchord.core.storage.KeyValueInMemoryStorage;
import ru.dht.dhtchord.core.storage.KeyValueStorage;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Slf4j
public class DhtNodeImpl implements DhtNode {

    private final int m;
    private final int nodeId;
    private final DhtNodeClient dhtNodeClient;

    private int predecessor;
    @Setter
    private FingerTable fingerTable;
    private final TreeMap<Integer, KeyValueStorage> storedData;
    private final TreeMap<Integer, ReadWriteLock> storedDataLocks;
    private Boolean isDataInitialized;

    public static DhtNode build(int m,
                                int nodeId,
                                TreeSet<Integer> nodes,
                                DhtNodeClient dhtNodeClient) {
        Utils.verifyNodesSetIsNotEmpty(nodes);

        int predecessorForNodeId = Predecessor.findPredecessor(nodes, nodeId);
        TreeMap<Integer, KeyValueStorage> storedData = new TreeMap<>();
        TreeMap<Integer, ReadWriteLock> storedDataLocks = new TreeMap<>();
        if (predecessorForNodeId == nodeId) {
            // The current node is responsible for the node IDs in the range (all keys): [0; 2 << m)
            for (int key = 0; key < (1 << m); key++) {
                becomeResponsibleForNodeData(nodeId, key, storedData, storedDataLocks);
            }
        } else {
            // The current node is responsible for the node IDs in the range: (predecessor; nodeId]
            int key = predecessorForNodeId + 1;
            while (key != nodeId + 1) {
                key = key % (1 << m);
                becomeResponsibleForNodeData(nodeId, key, storedData, storedDataLocks);
                key++;
            }
        }

        FingerTable fingerTable = buildFingerTable(m, nodeId, nodes);
        return new DhtNodeImpl(m, nodeId, dhtNodeClient, predecessorForNodeId, fingerTable, storedData, storedDataLocks, false);
    }

    @Override
    public synchronized void updateFingerTable(TreeSet<Integer> nodes) {
        log.info("Node (nodeId = {}) is updating finger table. Current version is {}. New list of nodes: {}",
                nodeId, fingerTable.getVersion(), nodes);
        Utils.verifyNodesSetIsNotEmpty(nodes);
        int predecessorForNodeId = Predecessor.findPredecessor(nodes, nodeId);
        // Adding the node IDs in the range: (predecessor; nodeId]
        for (int key = predecessorForNodeId + 1; key < nodeId; key++) {
            if (!storedData.containsKey(key)) {
                log.info("Node (nodeId = {}) is now responsible for the key {}", nodeId, key);
                storedData.put(key, new KeyValueInMemoryStorage());
                storedDataLocks.put(key, new ReentrantReadWriteLock(true));
            }
        }
        predecessor = predecessorForNodeId;
        fingerTable = buildFingerTable(m, nodeId, nodes);
        log.info("Node (nodeId = {}) updated finger table. New version is {}", nodeId, fingerTable.getVersion());
    }

    @Override
    public synchronized boolean initializeData(Map<Integer, Map<String, String>> data) {
        log.info("Transfer of data is started by the successor. Started initialization of node data (nodeId = {})", nodeId);

        for (var entry : data.entrySet()) {
            int key = entry.getKey();
            becomeResponsibleForNodeData(nodeId, key, storedData, storedDataLocks);

            // TODO: add methods putAll() and getAll()
            for (var dataEntry : entry.getValue().entrySet()) {
                String dataKey = dataEntry.getKey();
                String dataValue = dataEntry.getValue();
                boolean result = storeData(dataKey, dataValue);

                if (!result) {
                    log.error("Failed to initialize node data. Failed to insert: key = {} and value = {}",
                            dataKey, dataValue);
                    return false;
                }
            }
        }

        log.info("Transfer of data from the successor is successfully finished. Finished initialization of node data (nodeId = {})",
                nodeId);
        isDataInitialized = true;
        return true;
    }

    @Override
    public String getData(String key) {
        int requiredNode = HashUtils.getNodeIdForKey(key, m);
        if (getStoredKeys().contains(requiredNode)) {
            log.info("Getting data from the current node (nodeId = {}) cache by the key {}", nodeId, key);
            return getData(requiredNode, key);
        }
        return redirectGetRequest(key);
    }

    @Override
    public boolean storeData(String key, String value) {
        int requiredNode = HashUtils.getNodeIdForKey(key, m);
        if (getStoredKeys().contains(requiredNode)) {
            log.info("Saving key `{}` and value `{}` to the current node (nodeId = {})", nodeId, key, value);
            return storeData(requiredNode, key, value);
        }
        return redirectStoreRequest(key, value);
    }

    @Override
    public synchronized boolean transferDataToNode(int targetNodeId, Set<Integer> requestedKeys) {
        // TODO: validate requestedKeys
        log.info("Initialization before transfer of data keys ({}) to the node (targetNodeId = {})",
                requestedKeys, targetNodeId);

        List<Lock> writeLocks = new ArrayList<>(requestedKeys.size());
        requestedKeys.stream().map(storedDataLocks::get).forEach(lock -> writeLocks.add(lock.writeLock()));
        writeLocks.forEach(Lock::lock);

        try {
            TreeMap<Integer, KeyValueStorage> data = new TreeMap<>();
            for (int key : requestedKeys) {
                data.put(key, storedData.get(key));
            }
            log.info("Initialization completed. Started transfer of data keys ({}) to the node (targetNodeId = {})",
                    requestedKeys, targetNodeId);

            Map<Integer, Map<String, String>> dataToTransfer = new HashMap<>();
            data.forEach((currentNodeId, value) -> dataToTransfer.put(currentNodeId, value.getKeys().stream().collect(Collectors.toMap(
                            key -> key,
                            value::getData
                    ))
            ));

            boolean result = dhtNodeClient.transferDataToNode(targetNodeId, dataToTransfer);
            if (result) {
                log.info("Deleting the keys ({}) from the node (nodeId = {}) since transferred to another node (targetNodeId = {})",
                        nodeId, requestedKeys, targetNodeId);
                requestedKeys.forEach(this::removeKeyFromNodeData);
            }

            log.info("Finished transfer of keys ({}) from the node (nodeId = {}) to the target node (targetNodeId = {}). Result: {}",
                    requestedKeys, nodeId, targetNodeId, result);
            return result;
        } finally {
            writeLocks.forEach(Lock::unlock);
        }
    }

    @Override
    public Set<Integer> getStoredKeys() {
        return storedData.keySet();
    }

    @Override
    public int getSuccessor() {
        return  fingerTable
                .getFingerTable()
                .entrySet()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot get the successor from the finger table (the first entry is absent)"))
                .getValue();
    }

    @Override
    public int getPredecessor() {
        return predecessor;
    }

    private static FingerTable buildFingerTable(int m, int nodeId, TreeSet<Integer> nodes) {
        FingerTable fingerTable = FingerTable.buildTable(m, nodeId, nodes);
        log.info("Generated new finger table for the node (nodeId = {}): {}", nodeId, fingerTable);
        return fingerTable;
    }

    private static void becomeResponsibleForNodeData(int nodeId,
                                                     int targetNodeId,
                                                     TreeMap<Integer, KeyValueStorage> storedData,
                                                     TreeMap<Integer, ReadWriteLock> storedDataLocks) {
        log.info("Node (nodeId = {}) is now responsible for the key {}", nodeId, targetNodeId);
        storedData.put(targetNodeId, new KeyValueInMemoryStorage());
        storedDataLocks.put(targetNodeId, new ReentrantReadWriteLock(true));
    }

    private void removeKeyFromNodeData(int nodeIdToRemove) {
        log.info("Node (nodeId = {}) is not responsible for the key {}", nodeId, nodeIdToRemove);
        storedData.remove(nodeIdToRemove);
        storedDataLocks.remove(nodeIdToRemove);
    }

    private String getData(int requiredNode, String key) {
        Lock readLock = storedDataLocks.get(requiredNode).readLock();
        readLock.lock();

        String data;
        try {
            data = storedData.get(requiredNode).getData(key);
        } finally {
            readLock.unlock();
        }
        return data;
    }

    private boolean storeData(int requiredNode, String key, String value) {
        Lock writeLock = storedDataLocks.get(requiredNode).writeLock();
        writeLock.lock();

        boolean result;
        try {
            result = storedData.get(requiredNode).storeData(key, value);
        } finally {
            writeLock.unlock();
        }
        return result;
    }

    private boolean redirectStoreRequest(String key, String value) {
        int successorForKey = fingerTable.lookupSuccessorForKey(key);
        log.info("Key `{}` and value `{}` will not be put on the current node (nodeId = {}), redirecting request to the successor node (nodeId = {})",
                key, value, nodeId, successorForKey);
        return dhtNodeClient.storeDataToNode(successorForKey, key, value);
    }

    private String redirectGetRequest(String key) {
        int successorForKey = fingerTable.lookupSuccessorForKey(key);
        log.info("Key `{}` is not located on the current node (nodeId = {}), redirecting request to the successor node (nodeId = {})",
                key, nodeId, successorForKey);
        return dhtNodeClient.getDataFromNode(successorForKey, key);
    }

}
