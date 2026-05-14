package com.smartCity.complaintSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@SpringBootApplication
public class ComplaintSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComplaintSystemApplication.class, args);
	}

}
