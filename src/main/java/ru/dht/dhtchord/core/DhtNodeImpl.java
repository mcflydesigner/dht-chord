package ru.dht.dhtchord.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.connection.DhtNodeClient;
import ru.dht.dhtchord.core.hash.HashKey;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.core.storage.KeyValueStorage;

import java.util.Random;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Slf4j
public class DhtNodeImpl implements DhtNode {

    private static final Random random = new Random();

    private final DhtNodeMeta selfNode;
    private final DhtNodeClient dhtNodeClient;
    private FingerTable fingerTable;
    private KeyValueStorage storage;

    public static DhtNode buildSingleNode(HashSpace hashSpace,
                                          DhtNodeMeta selfMeta,
                                          DhtNodeClient dhtNodeClient,
                                          KeyValueStorage storage) {
        FingerTable fingerTable = FingerTable.buildForSingleNode(hashSpace, selfMeta);
        log.info("Generated new finger table for the node (nodeId = {}): {}", selfMeta.getKey(), fingerTable);
        return new DhtNodeImpl(selfMeta, dhtNodeClient, fingerTable, storage);
    }

    public static DhtNode join(HashSpace hashSpace,
                               DhtNodeMeta selfMeta,
                               DhtNodeAddress joinAddress,
                               DhtNodeClient dhtNodeClient,
                               KeyValueStorage storage) {
        HashKey start = hashSpace.add(selfMeta.getKey(), 1);
        DhtNodeMeta successor = dhtNodeClient.findSuccessor(new DhtNodeMeta(null, null, joinAddress), start);
        DhtNodeMeta predecessor = dhtNodeClient.updatePredecessor(successor, selfMeta);
        FingerTable fingerTable = FingerTable.buildForCluster(hashSpace,
                selfMeta, successor, predecessor, (hashKey) -> dhtNodeClient.findSuccessor(successor, hashKey));
        log.info("Generated new finger table for the node (nodeId = {}): {}", selfMeta.getKey(), fingerTable);
        return new DhtNodeImpl(selfMeta, dhtNodeClient, fingerTable, storage);
    }

    @Override
    public DhtNodeMeta updatePredecessor(DhtNodeMeta predecessor) {
        DhtNodeMeta oldPredecessor = fingerTable.getPredecessorNode();
        fingerTable.setPredecessorNode(predecessor);
        return oldPredecessor;
    }

    @Override
    public synchronized void stabilize() {
        log.info("Started stabilization of the node: {}", selfNode.getNodeId());

        DhtNodeMeta successor = fingerTable.getImmediateSuccessor();
        DhtNodeMeta predecessorOfSucc = dhtNodeClient.getPredecessor(successor);
        fingerTable.updateSuccessor(predecessorOfSucc);
        dhtNodeClient.notifyAboutPredecessor(selfNode, successor);

        log.info("Finished stabilization of the current node: {}", selfNode.getNodeId());
    }

    @Override
    public void notifyAboutPredecessor(DhtNodeMeta node) {
        fingerTable.updatePredecessor(node);
    }

    @Override
    public synchronized void fixFinger() {
        int randomIdx = random.nextInt(fingerTable.getHashSpace().getBitLength());
        log.debug("Fix finger is running for the index = {}", randomIdx);
        fingerTable.fixFinger(randomIdx, this::findSuccessor);
    }

    @Override
    public String getData(HashKey key) {
        log.info("Looking the key {} on the node {}", key, selfNode.getNodeId());
        DhtNodeMeta successor = findSuccessor(key);
        if (selfNode.getKey().equals(successor.getKey())) {
            log.info("The key {} is found locally on the node {}", key, selfNode.getNodeId());
            return storage.getData(key);
        }
        log.info("The current node {} found successor for the key: succ({}) = {}", key, selfNode.getNodeId(), successor.getNodeId());
        return dhtNodeClient.getDataFromNode(successor, key);
    }

    @Override
    public boolean storeData(HashKey key, String value) {
        DhtNodeMeta successor = findSuccessor(key);
        if (selfNode.getKey().equals(successor.getKey())) {
            log.info("The key ({}) and value ({}) stored locally on the node {}", key, value, selfNode.getNodeId());
            return storage.storeData(key, value);
        }
        log.info("The key ({}) and value ({}) will be stored on the successor node {}", key, value, successor.getNodeId());
        return dhtNodeClient.storeDataToNode(successor, key, value);
    }

    @Override
    public DhtNodeMeta findSuccessor(HashKey key) {
        if (fingerTable.isSuccessor(key)) {
            return selfNode;
        }

        DhtNodeMeta pred = fingerTable.findClosestPredecessor(key);
        if (selfNode.getKey().equals(pred.getKey())) {
            return fingerTable.getImmediateSuccessor();
        }
        return dhtNodeClient.findSuccessor(pred, key);
    }

    @Override
    public DhtNodeMeta getNodeMeta() {
        return selfNode;
    }

    @Override
    public DhtNodeMeta getPredecessor() {
        return fingerTable.getPredecessorNode();
    }

    @Override
    public DhtNodeMeta getSuccessor() {
        return fingerTable.getImmediateSuccessor();
    }

}
