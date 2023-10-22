package ru.dht.dhtchord.core.storage;

public interface KeyValueStorage {
    boolean storeData(String key, String value);

    String getData(String key);
}
