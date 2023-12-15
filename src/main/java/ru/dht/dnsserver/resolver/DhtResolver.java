package ru.dht.dnsserver.resolver;

import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;
import ru.dht.dnsserver.DhtUnhashedLocalClient;

public class DhtResolver implements DNSResolver {

    private final DhtUnhashedLocalClient dhtClient;

    public DhtResolver(DhtUnhashedLocalClient dhtClient) {
        this.dhtClient = dhtClient;
    }

    @Override
    @SneakyThrows
    public Message resolve(Message request) {
        Record question = request.getQuestion();

        String encAnswer = dhtClient.getData(recordKey(question));
        if (encAnswer == null || encAnswer.length() <= 0) {
            return request;
        }
        Record answer = Record.fromWire(Base64.decodeBase64(encAnswer), Section.ANSWER);
        request.addRecord(answer, Section.ANSWER);

        return request;
    }

    public static String recordKey(Record question) {
        return String.format("%s|%s|%s",
                question.getName().canonicalize().toString(),
                Type.string(question.getType()),
                DClass.string(question.getDClass())
                );
    }
}
