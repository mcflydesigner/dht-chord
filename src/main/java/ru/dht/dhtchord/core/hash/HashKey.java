package ru.dht.dhtchord.core.hash;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;
import java.util.HexFormat;
import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HashKey implements Comparable<HashKey> {
    private final byte[] value;
    private final BigInteger intValue;

    static HashKey of(BigInteger intValue, int bits) {
        BigInteger m = BigInteger.valueOf(2).pow(bits);
        intValue = intValue.mod(m);
        return of(intValue.toByteArray());
    }

    static HashKey of(byte[] bytes) {
        if (Objects.isNull(bytes)) {
            throw new IllegalArgumentException();
        }
        return new HashKey(
                bytes,
                new BigInteger(bytes)
        );
    }

    static HashKey fromString(String s) {
        return HashKey.of(HexFormat.of().parseHex(s));
    }

    BigInteger getIntValue() {
        return intValue;
    }

    public String toString() {
        return HexFormat.of().formatHex(value);
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
