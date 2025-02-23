package com.NxtWave;

import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class UserManagementApiApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(UserManagementApiApplication.class, args);

		if (context.containsBean("userServiceImpl")) {
            System.out.println("✅ UserServiceImpl is registered as a bean!");
        } else {
            System.out.println("❌ UserServiceImpl is NOT registered in the application context!");
        }
	}

}
