package ru.dht.dhtchord.core.hash;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public class SHA1HashSpace implements HashSpace {
    @Override
    public int getBitLength() {
        return Hashing.sha1().bits(); // 160 bits
    }

    private int getHexLength() {
        return getBitLength() / 4;
    }

    @Override
    public HashKey hash(String digest) {
        byte[] hash = Hashing.sha1().hashString(digest, StandardCharsets.UTF_8).asBytes();
        return HashKey.of(hash, this);
    }

    @Override
    public HashKey fromString(String s) {
        if (s.length() > getHexLength()) {
            s = s.substring(s.length() - getHexLength());
        }
        if (s.length() < getHexLength()) {
            s = Strings.padStart(s, getHexLength(), '0');
        }
        return HashKey.fromString(s, this);
    }

    @Override
    public HashKey add(HashKey hashKey, long i) {
        return HashKey.of(hashKey.getIntValue().add(BigInteger.valueOf(i)), getBitLength(), this);
    }

    @Override
    public HashKey add(HashKey hashKey, BigInteger i) {
        return HashKey.of(hashKey.getIntValue().add(i), getBitLength(), this);
    }

    @Override
    public String toString(byte[] value) {
        String s = HexFormat.of().formatHex(value);
        if (s.length() > getHexLength()) {
            s = s.substring(s.length() - getHexLength());
        }
        if (s.length() < getHexLength()) {
            s = Strings.padStart(s, getHexLength(), '0');
        }
        return s;
    }
}
