package ru.dht.dnsserver;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.ClassPathResource;
import org.xbill.DNS.Master;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import ru.dht.dnsserver.resolver.DhtResolver;


@Slf4j
@RequiredArgsConstructor
public class ZoneFileEnricher implements Runnable {

    private final boolean enabled;
    private final String zoneFilePath;
    private final DhtUnhashedLocalClient dhtClient;

    @SneakyThrows
    public void run() {
        if (!enabled) {
            log.info("Zone file enricher disabled");
            return;
        }
        Thread.sleep(2000);
        log.info("Going to fill DHT database with zone file {}", zoneFilePath);
        ClassPathResource zoneFile = new ClassPathResource(zoneFilePath);
        try (Master master = new Master(zoneFile.getInputStream())) {
            while (true) {
                Record rec = master.nextRecord();
                if (rec == null) break;
                log.info("Store record: {}", rec);
                String key = DhtResolver.recordKey(rec);
                String data = Base64.encodeBase64String(rec.toWire(Section.ANSWER));
                dhtClient.storeData(key, data);
            }
        }
    }
}
