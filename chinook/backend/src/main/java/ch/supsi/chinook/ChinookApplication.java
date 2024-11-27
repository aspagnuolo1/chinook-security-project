package ch.supsi.chinook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class ChinookApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChinookApplication.class, args);
	}
}
