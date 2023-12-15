package ru.dht.dnsserver.resolver;

import org.xbill.DNS.Message;
import org.xbill.DNS.Record;

public interface DNSResolver {
    Message resolve(Message question);
}
