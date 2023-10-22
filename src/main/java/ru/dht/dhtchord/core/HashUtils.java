package ru.dht.dhtchord.core;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HashUtils {

    public String sha1(String data) {
        return Hashing.sha1().hashString(data, Charsets.UTF_8).toString();
    }

    public int getNodeIdForKey(String key, int m) {
        return sha1(key).hashCode() % ((1 << m) - 1);
    }

}
