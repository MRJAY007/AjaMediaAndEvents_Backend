
package com.aja.mediaevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AjaBusinessBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AjaBusinessBackendApplication.class, args);
    }
}
