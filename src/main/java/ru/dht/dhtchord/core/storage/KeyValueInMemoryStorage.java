package ru.dht.dhtchord.core.storage;

import java.util.HashMap;
import java.util.Map;

public class KeyValueInMemoryStorage implements KeyValueStorage {
    private final Map<String, String> data = new HashMap<>();

    public boolean storeData(String key, String value) {
        data.put(key, value);
        return true;
    }

    public String getData(String key) {
        return data.get(key);
    }

}
