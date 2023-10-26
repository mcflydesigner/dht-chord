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
        ArrayList<FingerEntry> fingerTable = new ArrayList<>();

        for (int i = 0; i < hashSpace.getBitLength(); i++) {
            HashKey start = hashSpace.add(selfNode.getKey(), BigInteger.TWO.pow(i));
            fingerTable.add(new FingerEntry(start, null, null));
        }

        fingerTable.get(0).setNode(successor);

        for (int i = 0; i < fingerTable.size() - 1; i++) {
            FingerEntry fingerEntry = fingerTable.get(i);
            FingerEntry fingerEntryNext = fingerTable.get(i + 1);
            fingerEntry.setIntervalEnd(fingerEntryNext.getIntervalStart());

            if (intervalContains(selfNode.getKey(), fingerEntry.node.getKey(), fingerEntryNext.getIntervalStart())) {
                fingerEntryNext.setNode(fingerEntry.node);
            } else {
                fingerEntryNext.setNode(findSuccessor.apply(fingerEntryNext.getIntervalStart()));
            }
        }

        return new FingerTable(hashSpace, selfNode, predecessor, fingerTable, atomicCounter.incrementAndGet());
    }

    public static FingerTable buildForSingleNode(HashSpace hashSpace, DhtNodeMeta selfNode) {
        return new FingerTable(
                hashSpace,
                selfNode,
                selfNode,
                new ArrayList<>(),
                atomicCounter.incrementAndGet()
        );
    }

    public boolean isSuccessor(HashKey key) {
        return intervalContains(predecessorNode.getKey(), selfNode.getKey(), key);
    }

    public DhtNodeMeta findClosestPredecessor(HashKey key) {
        for (int i = fingerTable.size() - 1; i >= 0; i--) {
            FingerEntry f = fingerTable.get(i);
            if (intervalContains(f.intervalStart, f.intervalEnd, key)) {
                return f.node;
            }
        }
        return selfNode;
    }

    private static boolean intervalContains(HashKey start, HashKey end, HashKey key) {
        if (start.compareTo(end) < 0) {
            return start.compareTo(key) <= 0 && key.compareTo(end) < 0;
        } else {
            return start.compareTo(key) <= 0 || key.compareTo(end) < 0;
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
