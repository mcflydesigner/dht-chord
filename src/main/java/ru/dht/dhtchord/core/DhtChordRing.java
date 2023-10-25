package ru.dht.dhtchord.core;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.connection.DhtNodeClient;

import java.util.Collections;
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

    // Request to transfer keys from the successor node
    public synchronized boolean initDataCurrentNode() {
        int successor = dhtNode.getSuccessor();

        if (successor == dhtNodeMeta.getNodeId()) {
            log.warn("Skip to request data transfer from the successor node since the succ({}) = {}",
                    successor, successor);
            dhtNode.initializeData(Collections.emptyMap());
            return true;
        }

        Set<Integer> keys = dhtNode.getStoredKeys();
        log.info("Requesting successor node (targetNodeId = {}) to transfer the keys: {}",
                successor, keys);
        boolean result = dhtNodeClient.requestTransferDataToNode(successor, dhtNodeMeta.getNodeId(), keys);
        log.info("Requested successor node (targetNodeId = {}) to transfer the keys: {}. Result: {}",
                successor, keys, result);
        return result;
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

    public boolean requestToTransferDataToNode(int toNodeId,
                                               Set<Integer> requestedKeys) {
        // TODO: validate targetNodeId
        log.info("Node (targetNodeId = {}) requested to transfer a set of keys: {}", toNodeId, requestedKeys);

        if (!dhtNode.getStoredKeys().containsAll(requestedKeys)) {
            log.error("Node (targetNodeId = {}) requested to transfer keys that are not present on the current node (nodeId = {}): {}",
                    toNodeId, dhtNodeMeta.getNodeId(), Sets.difference(requestedKeys, dhtNode.getStoredKeys()));
            return false;
        }
        // Schedule transferring keys
        log.info("Scheduled transfer of keys ({}) to the node (targetNodeId = {})", requestedKeys, toNodeId);
        executor.execute(() -> performTransferKeysToNode(toNodeId, requestedKeys));

        return true;
    }

    private synchronized void performTransferKeysToNode(int toNodeId,
                                                        Set<Integer> requestedKeys) {
        log.info("Started transfer of keys ({}) to the node (targetNodeId = {})", requestedKeys, toNodeId);
        boolean result = dhtNode.transferDataToNode(toNodeId, requestedKeys);
        log.info("Attempted to transfer data keys ({}) to the node (targetNodeId = {}). Result = {}",
                requestedKeys, toNodeId, result);
    }

    private void registerCurrentNode(int targetNodeId) {
        log.info("Attempt to register current node (nodeId = {}) in the cluster topology. Target nodeId = {}",
                dhtNodeMeta.getNodeId(), targetNodeId);
        Boolean result = dhtNodeClient.registerNewNode(targetNodeId, dhtNodeMeta);
        log.info("Tried to register current node (nodeId = {}) in the cluster topology. Target nodeId = {}. Result = {}",
                dhtNodeMeta.getNodeId(), targetNodeId, result);
    }

}
