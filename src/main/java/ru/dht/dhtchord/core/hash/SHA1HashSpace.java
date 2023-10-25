package ru.dht.dhtchord.core.hash;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class SHA1HashSpace implements HashSpace {
    @Override
    public int getBitLength() {
        return Hashing.sha1().bits();
    }

    @Override
    public HashKey hash(String digest) {
        byte[] hash = Hashing.sha1().hashString(digest, StandardCharsets.UTF_8).asBytes();
        return HashKey.of(hash);
    }
}
