package ru.dht.dnsserver.resolver;

import org.xbill.DNS.Record;
import ru.dht.dhtchord.spring.client.DhtClient;

public class DhtResolver implements DNSResolver {

    private final DhtClient dhtClient;

    public DhtResolver(DhtClient dhtClient) {
        this.dhtClient = dhtClient;
    }

    @Override
    public Record resolve(Record question) {
        return null;
    }
}
