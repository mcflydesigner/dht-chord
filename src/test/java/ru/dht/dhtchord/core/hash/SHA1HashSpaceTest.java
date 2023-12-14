package ru.dht.dhtchord.core.hash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SHA1HashSpaceTest {

    private static final HashSpace hashSpace = new SHA1HashSpace();

    @Test
    @DisplayName("Correctly incrementing hash key 1 case")
    public void testInc1() {
        HashKey hashKey = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031647a", hashSpace);
        HashKey expected = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031647b", hashSpace);
        assertEquals(expected, hashSpace.add(hashKey, 1));
    }

    @Test
    @DisplayName("Correctly incrementing hash key 2 case")
    public void testInc2() {
        HashKey hashKey = HashKey.fromString("1fffffffffffffffffffffffffffffffffffffff", hashSpace);
        HashKey expected = HashKey.fromString("2000000000000000000000000000000000000000", hashSpace);
        assertEquals(expected, hashSpace.add(hashKey, 1));
    }

    @Test
    @DisplayName("Correctly incrementing hash key 3 case")
    public void testInc3() {
        HashKey hashKey = HashKey.fromString("2000000000000000000000000000000000000000", hashSpace);
        HashKey expected = HashKey.fromString("2000000000000000000000000000000000000001", hashSpace);
        assertEquals(expected, hashSpace.add(hashKey, 1));
    }

    @Test
    @DisplayName("Correctly incrementing hash key 4 case")
    public void testInc4() {
        HashKey hashKey = HashKey.fromString("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", hashSpace);
        HashKey expected = HashKey.fromString("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeef", hashSpace);
        assertEquals(expected, hashSpace.add(hashKey, 1));
    }

    @Test
    @DisplayName("Correctly incrementing hash key 5 case")
    public void testInc5() {
        HashKey hashKey = HashKey.fromString("fffffffffffffffffffffffffffffffffffffffe", hashSpace);
        HashKey expected = HashKey.fromString("ffffffffffffffffffffffffffffffffffffffff", hashSpace);
        assertEquals(expected, hashSpace.add(hashKey, 1));
    }

    @Test
    @DisplayName("Correctly incrementing hash key 6 case")
    public void testInc6() {
        HashKey hashKey = HashKey.fromString("ffffffffffffffffffffffffffffffffffffffff", hashSpace);
        HashKey expected = HashKey.fromString("0000000000000000000000000000000000000000", hashSpace);
        assertEquals(expected, hashSpace.add(hashKey, 1));
    }
}
