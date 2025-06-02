package br.com.fiap.Floodless;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FloodlessApplication {

	public static void main(String[] args) {
		SpringApplication.run(FloodlessApplication.class, args);
	}

}
