package ru.dht.dhtchord.core.hash;

import java.math.BigInteger;

public interface HashSpace {
    int getBitLength();
    HashKey hash(String digest);
    HashKey fromString(String s);
    HashKey add(HashKey hashKey, long i);
    HashKey add(HashKey hashKey, BigInteger i);
}
