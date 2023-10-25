package ru.dht.dhtchord.core.hash;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.math.BigInteger;
import java.util.HexFormat;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HashKey {
    private final byte[] value;
    private final BigInteger intValue;

    static HashKey of(byte[] bytes) {
        return new HashKey(
                bytes,
                new BigInteger(bytes)
        );
    }

    public String toString() {
        return HexFormat.of().formatHex(value);
    }
}
