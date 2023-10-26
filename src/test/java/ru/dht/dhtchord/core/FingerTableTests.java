package ru.dht.dhtchord.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.core.hash.SHA1HashSpace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FingerTableTests {

    private static HashSpace hashSpace = new SHA1HashSpace();
    private static final DhtNodeMeta node = new DhtNodeMeta("0", hashSpace.fromString("1234"), new DhtNodeAddress("localhost"));
    private static final String testKey = "1234567890123456789012345678901234567890";


    @Test
    @DisplayName("Testing finger table for single node cluster")
    public void testCase1() {
        FingerTable fingerTable = FingerTable.buildForSingleNode(hashSpace, node);
        assertEquals(160, fingerTable.getFingerTable().size());
        assertEquals(node.getKey(), fingerTable.getImmediateSuccessor().getKey());
        assertEquals(node.getKey(), fingerTable.getPredecessorNode().getKey());
        assertTrue(fingerTable.isSuccessor(hashSpace.fromString(testKey)));
    }

}
