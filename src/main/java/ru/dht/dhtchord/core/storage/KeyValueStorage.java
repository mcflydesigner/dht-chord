package ru.dht.dhtchord.core.storage;

import ru.dht.dhtchord.core.hash.HashKey;

import java.util.Set;

public interface KeyValueStorage {
    boolean storeData(HashKey key, String value);

    String getData(HashKey key);

    String removeData(HashKey key);

    Set<HashKey> getKeys();
}
