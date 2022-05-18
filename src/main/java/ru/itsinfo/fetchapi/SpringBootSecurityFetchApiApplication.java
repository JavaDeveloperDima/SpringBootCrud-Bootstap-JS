package ru.itsinfo.fetchapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.itsinfo.fetchapi.service.AppService;

@SpringBootApplication
public class SpringBootSecurityFetchApiApplication {
	@Autowired
	private AppService appService;
	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityFetchApiApplication.class, args);
	}

}
