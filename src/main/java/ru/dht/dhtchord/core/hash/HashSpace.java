package ru.dht.dhtchord.core.hash;

public interface HashSpace {
    int getBitLength();
    HashKey hash(String digest);
    HashKey fromString(String s);
}
