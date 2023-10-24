package ru.dht.dhtchord.core;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.connection.DhtNodeClient;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@AllArgsConstructor
@Slf4j
public class DhtChordRing {

    private final int m;
    private final DhtNodeMeta dhtNodeMeta;

    private final DhtNode dhtNode;
    private final TreeSet<Integer> nodes;
    private final DhtNodeClient dhtNodeClient;
    @Getter
    private final AtomicReference<Map<Integer, DhtNodeAddress>> nodeAddressesMapRef;

    private final Executor executor = Executors.newFixedThreadPool(5);

    // Update all other finger tables
    public synchronized void registerCurrentNode() {
        log.info("Started initialization of the current node (nodeId = {}) in the cluster topology",
                dhtNodeMeta.getNodeId());
        nodeAddressesMapRef.get().entrySet().stream()
                .filter(e -> e.getKey() != dhtNodeMeta.getNodeId())
                .forEach(e -> registerCurrentNode(e.getKey()));
        log.info("Finished initialization of the current node (nodeId = {}) in the cluster topology",
                dhtNodeMeta.getNodeId());
    }

    public synchronized boolean addNode(int newNodeId, DhtNodeAddress dhtNodeAddress) {
        // TODO: validate newNodeId
        log.info("Node (nodeId = {}) is registering new node: nodeId = {} and address = {}",
                dhtNodeMeta.getNodeId(), newNodeId, dhtNodeAddress.getAddress());
        nodeAddressesMapRef.get().put(newNodeId, dhtNodeAddress);
        nodes.add(newNodeId);
        dhtNode.updateFingerTable(nodes);
        return true;
    }

    public boolean requestToTransferDataToNode(int targetNodeId,
                                               Set<Integer> requestedKeys) {
        // TODO: validate targetNodeId
        log.info("Node (targetNodeId = {}) requested to transfer a set of keys: {}", targetNodeId, requestedKeys);

        if (!dhtNode.getStoredKeys().containsAll(requestedKeys)) {
            log.error("Node (targetNodeId = {}) requested to transfer keys that are not present on the current node (nodeId = {}): {}",
                    targetNodeId, dhtNodeMeta.getNodeId(), Sets.difference(requestedKeys, dhtNode.getStoredKeys()));
            return false;
        }
        // Schedule transferring keys
        log.info("Scheduled transfer of keys ({}) to the node (targetNodeId = {})", requestedKeys, targetNodeId);
        executor.execute(() -> performTransferKeysToNode(targetNodeId, requestedKeys));

        return true;
    }

    private synchronized void performTransferKeysToNode(int targetNodeId,
                                                        Set<Integer> requestedKeys) {
        log.info("Started transfer of keys ({}) to the node (targetNodeId = {})", requestedKeys, targetNodeId);
        boolean result = dhtNode.transferDataToNode(targetNodeId, requestedKeys);
        log.info("Attempted to transfer data keys ({}) to the node (targetNodeId = {}). Result = {}",
                requestedKeys, targetNodeId, result);
    }

    private void registerCurrentNode(int targetNodeId) {
        log.info("Attempt to register current node (nodeId = {}) in the cluster topology. Target nodeId = {}",
                dhtNodeMeta.getNodeId(), targetNodeId);
        Boolean result = dhtNodeClient.registerNewNode(targetNodeId, dhtNodeMeta);
        log.info("Tried to register current node (nodeId = {}) in the cluster topology. Target nodeId = {}. Result = {}",
                dhtNodeMeta.getNodeId(), targetNodeId, result);
    }

}
