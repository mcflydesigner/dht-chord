package ru.dht.dnsserver.resolver;

import org.xbill.DNS.Record;

public interface DNSResolver {
    Record resolve(Record question);
}
