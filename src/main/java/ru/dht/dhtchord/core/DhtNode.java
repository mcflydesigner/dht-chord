package ru.dht.dhtchord.core;

public interface DhtNode {
    String getData(String key);

    boolean storeData(String key, String value);
}
