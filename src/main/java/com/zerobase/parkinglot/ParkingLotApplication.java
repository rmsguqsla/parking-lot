package com.zerobase.parkinglot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ParkingLotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParkingLotApplication.class, args);
	}

}
