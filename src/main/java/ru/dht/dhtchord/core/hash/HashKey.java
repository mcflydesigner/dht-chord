package ru.dht.dhtchord.core.hash;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.HexFormat;
import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HashKey implements Comparable<HashKey> {
    private final byte[] value;
    private final BigInteger intValue;
    private final HashSpace hashSpace;

    static HashKey of(BigInteger intValue, int bits, HashSpace hashSpace) {
        // intValue - 160 bit / 20 byte
        BigInteger m = BigInteger.ONE.shiftLeft(bits); // m - 161 bit / 21 byte
        intValue = intValue.mod(m); // 20b mod 21b = 21b
        return of(intValue.toByteArray(), hashSpace);
    }

    static HashKey of(byte[] bytes, HashSpace hashSpace) {
        if (Objects.isNull(bytes)) {
            throw new IllegalArgumentException();
        }

        byte[] bigIntegerBytes = new byte[bytes.length + 1];
        bigIntegerBytes[0] = 0; // Defer from negative BigInteger numbers
        System.arraycopy(bytes, 0, bigIntegerBytes, 1, bytes.length);
        return new HashKey(
                bytes,
                new BigInteger(bigIntegerBytes),
                hashSpace
        );
    }

    static HashKey fromString(String s, HashSpace hashSpace) {
        return HashKey.of(HexFormat.of().parseHex(s), hashSpace);
    }

    BigInteger getIntValue() {
        return intValue;
    }

    public String toString() {
        return hashSpace.toString(value);
    }

    @Override
    public int compareTo(HashKey o) {
        return intValue.compareTo(o.intValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashKey hashKey = (HashKey) o;
        return intValue.equals(hashKey.intValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intValue);
    }
}
