package ru.dht.dhtchord.core.storage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage implements KeyValueStorage {
    private final Map<String, String> storage = new ConcurrentHashMap<>();

    @Override
    public boolean storeData(String key, String value) {
        storage.put(key, value);
        return true;
    }

    @Override
    public String getData(String key) {
        return storage.get(key);
    }

    @Override
    public Set<String> getKeys() {
        return storage.keySet();
    }
}
