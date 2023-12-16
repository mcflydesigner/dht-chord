package ru.dht.dhtchord.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.connection.DhtNodeClient;
import ru.dht.dhtchord.core.hash.HashKey;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.core.storage.KeyValueStorage;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Slf4j
public class DhtNodeImpl implements DhtNode {

    private static final Random random = new Random();

    private final DhtNodeMeta selfNode;
    private final DhtNodeClient dhtNodeClient;
    private final HashSpace hashSpace;
    private FingerTable fingerTable;
    private KeyValueStorage storage;

    public static DhtNode buildSingleNode(HashSpace hashSpace,
                                          DhtNodeMeta selfMeta,
                                          DhtNodeClient dhtNodeClient,
                                          KeyValueStorage storage) {
        FingerTable fingerTable = FingerTable.buildForSingleNode(hashSpace, selfMeta);
        log.info("Generated new finger table for the node (nodeId = {}): {}", selfMeta.getKey(), fingerTable);
        return new DhtNodeImpl(selfMeta, dhtNodeClient, hashSpace, fingerTable, storage);
    }

    public static DhtNode join(HashSpace hashSpace,
                               DhtNodeMeta selfMeta,
                               DhtNodeAddress joinAddress,
                               DhtNodeClient dhtNodeClient,
                               KeyValueStorage storage) {
        log.info("Joining the cluster topology...");
        HashKey start = hashSpace.add(selfMeta.getKey(), 1);
        DhtNodeMeta successor = dhtNodeClient.findSuccessor(new DhtNodeMeta(null, null, joinAddress), start);
        log.info("Got successor of {} is node {}", selfMeta.getKey(), successor);
        transferData(hashSpace, selfMeta, successor, dhtNodeClient, storage);
        DhtNodeMeta predecessor =  dhtNodeClient.updatePredecessor(successor, selfMeta);
        FingerTable fingerTable = FingerTable.buildForCluster(hashSpace,
                selfMeta, successor, predecessor, (hashKey) -> dhtNodeClient.findSuccessor(successor, hashKey));
        log.info("Generated new finger table for the node (nodeId = {}): {}", selfMeta.getKey(), fingerTable);
        log.info("Joined the cluster topology!");
        return new DhtNodeImpl(selfMeta, dhtNodeClient, hashSpace, fingerTable, storage);
    }

    private static void transferData(HashSpace hashSpace, DhtNodeMeta selfMeta, DhtNodeMeta successor,
                                     DhtNodeClient dhtNodeClient, KeyValueStorage storage) {
        log.info("Requested to transfer data from the successor {}", successor);
        Map<String, String> data = dhtNodeClient.getDataToTransfer(successor, selfMeta);
        log.info("Successfully transferred data from the successor (amount: {}): {}", data.size(), data);
        data.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .forEach(e -> storage.storeData(hashSpace.fromString(e.getKey()), e.getValue()));
        dhtNodeClient.confirmDataTransfer(successor, selfMeta);
        log.info("Successfully confirmed data transfer to the successor: {}", data);
        log.info("Finished transfer of the data from the successor {}", successor);
    }

    @Override
    public DhtNodeMeta updatePredecessor(DhtNodeMeta predecessor) {
        DhtNodeMeta oldPredecessor = fingerTable.getPredecessorNode();
        fingerTable.setPredecessorNode(predecessor);
        log.info("Updated node predecessor. Old value: {}. New value: {}", oldPredecessor, predecessor);
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
        log.info("Updated node predecessor. New value: {}", node);
        fingerTable.updatePredecessor(node);
    }

    @Override
    public synchronized void fixFinger() {
//        int randomIdx = random.nextInt(fingerTable.getHashSpace().getBitLength());
//        log.debug("Fix finger is running for the index = {}", randomIdx);
        log.debug("Fix finger is running for all ids");
        for (int i = 0; i < fingerTable.getFingerTable().size(); i++) {
            fingerTable.fixFinger(i, this::findSuccessor);
        }
        log.debug("Fixed finger table: {}", fingerTable);
    }

    @Override
    public String getData(HashKey key) {
        log.info("Looking the key {} on the node {}", key, selfNode.getNodeId());
        DhtNodeMeta successor = findSuccessor(key);
        if (selfNode.getKey().equals(successor.getKey())) {
            log.info("The key {} belongs to the node {}", key, selfNode.getNodeId());
            return storage.getData(key);
        }
        log.info("The current node {} found successor for the key: succ({}) = {}", selfNode.getNodeId(), key, successor.getNodeId());
        return dhtNodeClient.getDataFromNode(successor, key);
    }

    @Override
    public Map<String, String> getDataToTransfer(DhtNodeMeta dhtNodeMeta) {
        Pair<HashKey, HashKey> keyRange = getKeysRangeToTransfer(dhtNodeMeta);
        HashKey rangeStart = keyRange.getLeft();
        HashKey rangeEnd = keyRange.getRight();
        log.info("Node {} requested to transfer data. Key range is: [{}, {}]",
                dhtNodeMeta.getNodeId(), rangeStart, rangeEnd);

        Map<String, String> result =  storage.getEntries().entrySet().stream()
                .filter(entry -> intervalContains(rangeStart, rangeEnd, entry.getKey()))
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));
        log.info("Transferring data to node {}: {}", dhtNodeMeta.getNodeId(), result);
        return result;
    }

    @Override
    public boolean confirmDataTransfer(DhtNodeMeta dhtNodeMeta) {
        Pair<HashKey, HashKey> keyRange = getKeysRangeToTransfer(dhtNodeMeta);
        HashKey rangeStart = keyRange.getLeft();
        HashKey rangeEnd = keyRange.getRight();
        log.info("Node {} finished data transfer. Key range to be removed from local storage is: [{}, {}]",
                dhtNodeMeta.getNodeId(), rangeStart, rangeEnd);

        storage.getKeys().stream()
                .filter(key -> intervalContains(rangeStart, rangeEnd, key))
                .peek(key -> log.info("Removing key {} from local storage", key))
                .forEach(key -> storage.removeData(key));
        return true;
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
        log.debug("Looking for successor of {}", key);
        if (fingerTable.isSuccessor(key)) {
            return selfNode;
        }

        DhtNodeMeta pred = fingerTable.findClosestPredecessor(key);
        log.debug("Predecessor for key {} is {}", key, pred.getKey());
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

    private Pair<HashKey, HashKey> getKeysRangeToTransfer(DhtNodeMeta dhtNodeMeta) {
        DhtNodeMeta predecessor = getPredecessor();
        HashKey rangeStart = hashSpace.add(predecessor.getKey(), 1);
        HashKey rangeEnd = dhtNodeMeta.getKey();
        return Pair.of(rangeStart, rangeEnd);
    }

    private static boolean intervalContains(HashKey start, HashKey end, HashKey key) {
        if (start.compareTo(end) < 0) {
            return start.compareTo(key) <= 0 && key.compareTo(end) <= 0;
        } else {
            return start.compareTo(key) <= 0 || key.compareTo(end) <= 0;
        }
    }
}
