package ru.dht.dhtchord.core.storage;

import ru.dht.dhtchord.core.hash.HashKey;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage implements KeyValueStorage {
    private final Map<HashKey, String> storage = new ConcurrentHashMap<>();

    @Override
    public boolean storeData(HashKey key, String value) {
        storage.put(key, value);
        return true;
    }

    @Override
    public String getData(HashKey key) {
        return storage.get(key);
    }

    @Override
    public Set<HashKey> getKeys() {
        return storage.keySet();
    }
}
