package com.literatememory.linktree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LinktreeCloneApplication {
    public static void main(String[] args) {
        SpringApplication.run(LinktreeCloneApplication.class, args);
    }
}
