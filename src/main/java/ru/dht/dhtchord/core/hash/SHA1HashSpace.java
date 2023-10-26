package ru.dht.dhtchord.core.hash;

import com.google.common.hash.Hashing;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
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

    @Override
    public HashKey fromString(String s) {
        s = StringUtils.trimLeadingCharacter(s, '0');
        if (s.length() * 4 > getBitLength()) {
            throw new IllegalArgumentException(String.format("Key has invalid hash space = %s", s));
        }
        return HashKey.fromString(s);
    }

    @Override
    public HashKey add(HashKey hashKey, long i) {
        return HashKey.of(hashKey.getIntValue().add(BigInteger.valueOf(i)), getBitLength());
    }

    @Override
    public HashKey add(HashKey hashKey, BigInteger i) {
        return HashKey.of(hashKey.getIntValue().add(i), getBitLength());
    }
}
