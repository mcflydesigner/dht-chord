package ru.dht.dhtchord.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.hash.HashKey;
import ru.dht.dhtchord.core.hash.HashSpace;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class FingerTable {

    private static final AtomicInteger atomicCounter = new AtomicInteger(0);

    private final HashSpace hashSpace;
    private final HashKey selfKey;
    private DhtNodeMeta predecessorNode;
    @Getter
    private final List<DhtNodeMeta> fingerTable;
    @Getter
    private final int version;

    public DhtNodeMeta getImmediateSuccessor() {
        return fingerTable.get(0);
    }

//    public static FingerTable buildTable(HashSpace hashSpace, HashKey selfKey,
//                                         TreeMap<HashKey, DhtNodeMeta> knownNodes) {
//        Utils.verifyNodesSetIsNotEmpty(knownNodes);
//
//        LinkedList<DhtNodeMeta> fingers = new LinkedList<>();
//        for (int i = 0; i < hashSpace.getBitLength(); i++) {
//
//        }
//        return new FingerTable(m, nodeId, nodes, fingers, atomicCounter.incrementAndGet());
//    }

    public static FingerTable buildForSingleNode(HashSpace hashSpace, DhtNodeMeta selfNode) {
        LinkedList<DhtNodeMeta> fingers = new LinkedList<>();
        fingers.add(selfNode);
        return new FingerTable(
                hashSpace,
                selfNode.getKey(),
                selfNode,
                fingers,
                atomicCounter.incrementAndGet()
        );
    }

    private static DhtNodeMeta successor(HashKey key, TreeMap<HashKey, DhtNodeMeta> knownNodes) {

    }

    public int lookupSuccessorForKey(String key) {
       int nodeIdForKey = HashUtils.getNodeIdForKey(key, m);
       int maxLess = nodes.first();
       // TODO: reversed iterator faster?
       for (var fingerTableEntry : fingerTable.entrySet()) {
           int k = fingerTableEntry.getKey();
           int successor = fingerTableEntry.getValue();

           if (k == nodeIdForKey) {
               return successor;
           } else if (nodeIdForKey > k) {
               maxLess = k;
           }
       }
       return maxLess;
    }

}
