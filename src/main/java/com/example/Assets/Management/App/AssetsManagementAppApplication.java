package com.example.Assets.Management.App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AssetsManagementAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetsManagementAppApplication.class, args);
	}

}
