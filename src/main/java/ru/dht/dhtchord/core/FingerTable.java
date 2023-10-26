package ru.dht.dhtchord.core;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.hash.HashKey;
import ru.dht.dhtchord.core.hash.HashSpace;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class FingerTable {

    private static final AtomicInteger atomicCounter = new AtomicInteger(0);

    @Getter
    private final HashSpace hashSpace;
    private final DhtNodeMeta selfNode;
    @Getter
    @Setter
    private DhtNodeMeta predecessorNode;
    @Getter
    private final List<FingerEntry> fingerTable;
    @Getter
    private final int version;

    public DhtNodeMeta getImmediateSuccessor() {
        return fingerTable.isEmpty() ? selfNode : fingerTable.get(0).getNode();
    }

    public static FingerTable buildForCluster(HashSpace hashSpace,
                                              DhtNodeMeta selfNode,
                                              DhtNodeMeta successor,
                                              DhtNodeMeta predecessor,
                                              Function<HashKey, DhtNodeMeta> findSuccessor) {
        List<FingerEntry> fingerTable = initFingerTable(hashSpace, selfNode);

        fingerTable.get(0).setNode(successor);
        for (int i = 0; i < fingerTable.size() - 1; i++) {
            FingerEntry fingerEntry = fingerTable.get(i);
            FingerEntry fingerEntryNext = fingerTable.get(i + 1);

            if (intervalContainsLeft(selfNode.getKey(), fingerEntry.node.getKey(), fingerEntryNext.getIntervalStart())) {
                fingerEntryNext.setNode(fingerEntry.node);
            } else {
                fingerEntryNext.setNode(findSuccessor.apply(fingerEntryNext.getIntervalStart()));
            }
        }

        return new FingerTable(hashSpace, selfNode, predecessor, fingerTable, atomicCounter.incrementAndGet());
    }

    private static List<FingerEntry> initFingerTable(HashSpace hashSpace, DhtNodeMeta selfNode) {
        ArrayList<FingerEntry> fingerTable = new ArrayList<>();
        for (int i = 0; i < hashSpace.getBitLength(); i++) {
            HashKey start = hashSpace.add(selfNode.getKey(), BigInteger.ONE.shiftLeft(i));
            fingerTable.add(new FingerEntry(start, null, null));
            if (i > 0) {
                fingerTable.get(i - 1).setIntervalEnd(start);
            }
        }
        fingerTable.get(fingerTable.size() - 1).setIntervalEnd(selfNode.getKey());

        return fingerTable;
    }

    public static FingerTable buildForSingleNode(HashSpace hashSpace, DhtNodeMeta selfNode) {
        List<FingerEntry> fingerTable = initFingerTable(hashSpace, selfNode);
        for (FingerEntry fingerEntry : fingerTable) {
            fingerEntry.setNode(selfNode);
        }

        return new FingerTable(
                hashSpace,
                selfNode,
                selfNode,
                fingerTable,
                atomicCounter.incrementAndGet()
        );
    }

    public boolean isSuccessor(HashKey key) {
        return intervalContainsRight(predecessorNode.getKey(), selfNode.getKey(), key);
    }

    public DhtNodeMeta findClosestPredecessor(HashKey key) {
        for (int i = fingerTable.size() - 1; i >= 0; i--) {
            FingerEntry f = fingerTable.get(i);
            if (intervalContainsLeft(f.intervalStart, f.intervalEnd, key)) {
                return f.node;
            }
        }
        return selfNode;
    }

    public void updateSuccessor(DhtNodeMeta successor) {
        if (intervalContainsRight(selfNode.getKey(), getImmediateSuccessor().getKey(), successor.getKey())) {
            fingerTable.get(0).setNode(successor);
        }
    }

    public void updatePredecessor(DhtNodeMeta node) {
        if (intervalContainsLeft(getPredecessorNode().getKey(), selfNode.getKey(), node.getKey())) {
            predecessorNode = node;
        }
    }

    public void fixFinger(int index, Function<HashKey, DhtNodeMeta> findSuccessor) {
        FingerEntry fingerEntry = fingerTable.get(index);
        fingerEntry.setNode(findSuccessor.apply(fingerEntry.intervalStart));
    }

    private static boolean intervalContainsLeft(HashKey start, HashKey end, HashKey key) {
        if (start.compareTo(end) < 0) {
            return start.compareTo(key) <= 0 && key.compareTo(end) < 0;
        } else {
            return start.compareTo(key) <= 0 || key.compareTo(end) < 0;
        }
    }

    private static boolean intervalContainsRight(HashKey start, HashKey end, HashKey key) {
        if (start.compareTo(end) < 0) {
            return start.compareTo(key) < 0 && key.compareTo(end) <= 0;
        } else {
            return start.compareTo(key) < 0 || key.compareTo(end) <= 0;
        }
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @AllArgsConstructor
    private static class FingerEntry {
        HashKey intervalStart;
        HashKey intervalEnd;
        DhtNodeMeta node;
    }
}
