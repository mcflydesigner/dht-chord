package ru.dht.dnsserver.resolver;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;
import ru.dht.dnsserver.DhtUnhashedLocalClient;

@Slf4j
public class DhtResolver implements DNSResolver {

    private final DhtUnhashedLocalClient dhtClient;

    public DhtResolver(DhtUnhashedLocalClient dhtClient) {
        this.dhtClient = dhtClient;
    }

    @Override
    @SneakyThrows
    public Message resolve(Message request) {
        request.getHeader().setFlag(Flags.QR); // this is a response
        request.getHeader().setFlag(Flags.AA); // authoritative answer

        try {
            Record question = request.getQuestion();

            String encAnswer = dhtClient.getData(recordKey(question));
            if (encAnswer == null || encAnswer.length() <= 0) {
                request.getHeader().setRcode(Rcode.NXDOMAIN);
                return request;
            }
            Record answer = Record.fromWire(Base64.decodeBase64(encAnswer), Section.ANSWER);
            request.addRecord(answer, Section.ANSWER);
        } catch (Exception e) {
            log.error("Error while answering query {}", request, e);
            request.getHeader().setRcode(Rcode.SERVFAIL);
        }

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
