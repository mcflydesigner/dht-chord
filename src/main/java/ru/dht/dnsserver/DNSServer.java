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

@Slf4j
public class DNSServer implements Runnable {
    private final DatagramSocket socket;
    private byte[] buffer;
    private static final int BUF_LEN = 512;

    public DNSServer(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    @SneakyThrows
    public void run() {
        try {
            while (true) {
                buffer = new byte[BUF_LEN];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                int srcPort = packet.getPort();
                InetAddress srcAddress = packet.getAddress();
                Message message = new Message(packet.getData());
                log.info("Received message from {} {}:", srcAddress.toString(), srcPort);
                log.info("\n{}", message);

                Record question = message.getQuestion();
                ARecord answer = new ARecord(question.getName(),
                        question.getDClass(),
                        question.getTTL(),
                        InetAddress.getByName("1.1.1.1"));
                message.addRecord(answer, Section.ANSWER);
//                message.getHeader().setOpcode(Opcode.);
                buffer = message.toWire(BUF_LEN);
                packet = new DatagramPacket(buffer, buffer.length, srcAddress, srcPort);
                socket.send(packet);
            }
        } finally {
            socket.close();
        }
    }
}
