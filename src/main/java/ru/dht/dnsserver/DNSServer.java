package ru.dht.dnsserver;

import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;
import ru.dht.dnsserver.resolver.DNSResolver;

@Slf4j
public class DNSServer implements Runnable {
    private final DatagramSocket socket;
    private final DNSResolver resolver;
    private byte[] buffer;
    private static final int BUF_LEN = 512;

    public DNSServer(int port, DNSResolver resolver) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.resolver = resolver;
    }

    @SneakyThrows
    public void run() {
        InetAddress srcAddress;
        int srcPort;

        try {
            while (true) {
                buffer = new byte[BUF_LEN];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                srcPort = packet.getPort();
                srcAddress = packet.getAddress();
                Message message = new Message(packet.getData());
                log.info("Received message from {} {}:", srcAddress.toString(), srcPort);
                log.info("\n{}", message);

                message = resolver.resolve(message);
                buffer = message.toWire(BUF_LEN);
                packet = new DatagramPacket(buffer, buffer.length, srcAddress, srcPort);
                socket.send(packet);
            }
        } finally {
            socket.close();
        }
    }
}
