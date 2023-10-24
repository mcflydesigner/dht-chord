package ru.dht.dhtchord.core.storage;

import java.util.Set;

public interface KeyValueStorage {
    boolean storeData(String key, String value);

    String getData(String key);

    Set<String> getKeys();
}
