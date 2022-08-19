package com.example.demo;

import com.example.demo.services.InitializeDbService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    private final InitializeDbService initializeDbService;

    public DemoApplication(InitializeDbService initializeDbService) {
        this.initializeDbService = initializeDbService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            initializeDbService.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
