package ru.dht.dhtchord.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.connection.DhtNodeClient;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.core.hash.SHA1HashSpace;
import ru.dht.dhtchord.core.storage.KeyValueStorage;
import ru.dht.dhtchord.spring.client.DhtClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class DhtNodeImplTest {

    private static HashSpace hashSpace = new SHA1HashSpace();
    private static final DhtNodeMeta node = new DhtNodeMeta("0", hashSpace.fromString("1234"), new DhtNodeAddress("localhost"));
    private static final DhtNodeClient dhtNodeClient = new DhtNodeClient(Mockito.mock(DhtClient.class));
    private static final KeyValueStorage keyValueStorage = Mockito.mock(KeyValueStorage.class);
    private static final String testKey = "1234567890123456789012345678901234567890";

    @Test
    @DisplayName("Testing successor and predecessor of the node")
    public void testSingleNodeSuccAndPred() {
        DhtNode dhtNode = DhtNodeImpl.buildSingleNode(hashSpace, node, dhtNodeClient, keyValueStorage);
        assertEquals(node.getKey(), dhtNode.getSuccessor().getKey());
        assertEquals(node.getKey(), dhtNode.getPredecessor().getKey());
    }

    @Test
    @DisplayName("Testing store method of a single-node cluster")
    public void testSingleNodeSave() {
        DhtNode dhtNode = DhtNodeImpl.buildSingleNode(hashSpace, node, dhtNodeClient, keyValueStorage);
        dhtNode.storeData(hashSpace.fromString(testKey), "testValue");
        verify(keyValueStorage).storeData(hashSpace.fromString(testKey), "testValue");
    }

    @Test
    @DisplayName("Testing get method of a single-node cluster")
    public void testSingleNodeGet() {
        DhtNode dhtNode = DhtNodeImpl.buildSingleNode(hashSpace, node, dhtNodeClient, keyValueStorage);
        when(keyValueStorage.getData(hashSpace.fromString(testKey))).thenReturn("testValue");

        assertEquals("testValue", dhtNode.getData(hashSpace.fromString(testKey)));
        verify(keyValueStorage).getData(hashSpace.fromString(testKey));
    }

}
