package ru.dht.dhtchord.core.hash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HashKeyTest {

    private static final HashSpace hashSpace = new SHA1HashSpace();

    @Test
    @DisplayName("Correct initialization of the key from byte array 1 case")
    public void correctInitFromByteArray1() {
        HashKey hashKey = HashKey.of(new byte[]{0}, hashSpace);
        assertEquals(0, hashKey.getIntValue().compareTo(BigInteger.ZERO));
    }

    @Test
    @DisplayName("Correct initialization of the key from byte array 2 case")
    public void correctInitFromByteArray2() {
        HashKey hashKey = HashKey.of(new byte[]{1}, hashSpace);
        assertTrue(hashKey.getIntValue().compareTo(BigInteger.ZERO) >= 1);
    }

    @Test
    @DisplayName("Correct initialization of the key from byte array 3 case")
    public void correctInitFromByteArray3() {
        HashKey hashKey = HashKey.of(new byte[]{1, 2, 3}, hashSpace);
        assertTrue(hashKey.getIntValue().compareTo(BigInteger.ZERO) >= 1);
    }

    @Test
    @DisplayName("Correct initialization of the key from byte array 4 case")
    public void correctInitFromByteArray4() {
        HashKey hashKey = HashKey.of(
                new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20},
                hashSpace);
        assertTrue(hashKey.getIntValue().compareTo(BigInteger.ZERO) >= 1);
    }

    @Test
    @DisplayName("Correct initialization of the key fromString() 1 case")
    public void correctInitFromString1() {
        HashKey hashKey = HashKey.fromString("ffffffffffffffffffffffffffffffffffffffff", hashSpace);
        assertTrue(hashKey.getIntValue().compareTo(BigInteger.ZERO) >= 1);
    }

    @Test
    @DisplayName("Correct initialization of the key fromString() 2 case")
    public void correctInitFromString2() {
        HashKey hashKey = HashKey.fromString("b6589fc6ab0dc82cf12099d1c2d40ab994e8410c", hashSpace);
        assertTrue(hashKey.getIntValue().compareTo(BigInteger.ZERO) >= 1);
    }

    @Test
    @DisplayName("Correct initialization of the key fromString() 3 case")
    public void correctInitFromString3() {
        HashKey hashKey = HashKey.fromString("0000000000000000000000000000000000000000", hashSpace);
        assertEquals(0, hashKey.getIntValue().compareTo(BigInteger.ZERO));
    }

    @Test
    @DisplayName("Correct initialization of the key fromString() 4 case")
    public void correctInitFromString4() {
        HashKey hashKey = HashKey.fromString("0000000000000000000000000000000000000001", hashSpace);
        assertTrue(hashKey.getIntValue().compareTo(BigInteger.ZERO) >= 1);
    }

    @Test
    @DisplayName("Correctly comparing two hash keys 1 case")
    public void correctCompare1() {
        HashKey hashKey1 = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031647a", hashSpace);
        HashKey hashKey2 = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031647b", hashSpace);
        assertTrue(hashKey2.compareTo(hashKey1) >= 1);
    }

    @Test
    @DisplayName("Correctly comparing two hash keys 2 case")
    public void correctCompare2() {
        HashKey hashKey1 = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031647a", hashSpace);
        HashKey hashKey2 = HashKey.fromString("1b6453892473a467d07372d45eb05abc20316480", hashSpace);
        assertTrue(hashKey2.compareTo(hashKey1) >= 1);
    }

    @Test
    @DisplayName("Correctly comparing two hash keys 3 case")
    public void correctCompare3() {
        HashKey hashKey1 = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031647a", hashSpace);
        HashKey hashKey2 = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031647a", hashSpace);
        assertEquals(0, hashKey2.compareTo(hashKey1));
    }

    @Test
    @DisplayName("Correctly comparing two hash keys 4 case")
    public void correctCompare4() {
        HashKey hashKey1 = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031647a", hashSpace);
        HashKey hashKey2 = HashKey.fromString("2b6453892473a467d07372d45eb05abc2031647a", hashSpace);
        assertTrue(hashKey2.compareTo(hashKey1) >= 1);
    }

    @Test
    @DisplayName("Correctly comparing two hash keys 5 case")
    public void correctCompare5() {
        HashKey hashKey1 = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031647a", hashSpace);
        HashKey hashKey2 = HashKey.fromString("fb6453892473a467d07372d45eb05abc2031648a", hashSpace);
        assertTrue(hashKey2.compareTo(hashKey1) >= 1);
    }

    @Test
    @DisplayName("Correctly comparing two hash keys 6 case")
    public void correctCompare6() {
        HashKey hashKey1 = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031647a", hashSpace);
        HashKey hashKey2 = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031648a", hashSpace);
        assertTrue(hashKey2.compareTo(hashKey1) >= 1);
    }

    @Test
    @DisplayName("Correctly comparing two hash keys 7 case")
    public void correctCompare7() {
        HashKey hashKey1 = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031647a", hashSpace);
        HashKey hashKey2 = HashKey.fromString("b6589fc6ab0dc82cf12099d1c2d40ab994e8410c", hashSpace);
        assertTrue(hashKey2.compareTo(hashKey1) >= 1);
    }

    @Test
    @DisplayName("Correctly converting hash key to string using toString() method 1 case")
    public void correctToString1() {
        HashKey hashKey = HashKey.of(new byte[]{0}, hashSpace);
        assertEquals("0000000000000000000000000000000000000000", hashKey.toString());
    }

    @Test
    @DisplayName("Correctly converting hash key to string using toString() method 2 case")
    public void correctToString2() {
        HashKey hashKey = HashKey.of(new byte[]{1}, hashSpace);
        assertEquals("0000000000000000000000000000000000000001", hashKey.toString());
    }

    @Test
    @DisplayName("Correctly converting hash key to string using toString() method 3 case")
    public void correctToString3() {
        HashKey hashKey = HashKey.of(new byte[]{1,}, hashSpace);
        assertEquals("0000000000000000000000000000000000000001", hashKey.toString());
    }

    @Test
    @DisplayName("Correctly converting hash key to string using toString() method 4 case")
    public void correctToString4() {
        HashKey hashKey = HashKey.of(new byte[]{1, 2, 3}, hashSpace);
        assertEquals("0000000000000000000000000000000000010203", hashKey.toString());
    }

    @Test
    @DisplayName("Correctly converting hash key to string using toString() method 5 case")
    public void correctToString5() {
        HashKey hashKey = HashKey.fromString("1b6453892473a467d07372d45eb05abc2031648a", hashSpace);
        assertEquals("1b6453892473a467d07372d45eb05abc2031648a", hashKey.toString());
    }

    @Test
    @DisplayName("Correctly converting hash key to string using toString() method 6 case")
    public void correctToString6() {
        HashKey hashKey = HashKey.fromString("ffffffffffffffffffffffffffffffffffffffff", hashSpace);
        assertEquals("ffffffffffffffffffffffffffffffffffffffff", hashKey.toString());
    }

    @Test
    @DisplayName("Correctly converting hash key to string using toString() method 7 case")
    public void correctToString7() {
        HashKey hashKey = HashKey.fromString("0000000000000000000000000000000000000000", hashSpace);
        assertEquals("0000000000000000000000000000000000000000", hashKey.toString());
    }

    @Test
    @DisplayName("Correctly converting hash key to string using toString() method 8 case")
    public void correctToString8() {
        HashKey hashKey = hashSpace.add(hashSpace.fromString("b6589fc6ab0dc82cf12099d1c2d40ab994e8410c"), 1);
        assertEquals("b6589fc6ab0dc82cf12099d1c2d40ab994e8410d", hashKey.toString());
    }

}
