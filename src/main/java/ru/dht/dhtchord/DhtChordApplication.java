package ru.dht.dhtchord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DhtChordApplication {

	public static void main(String[] args) {
		SpringApplication.run(DhtChordApplication.class, args);
	}

}
