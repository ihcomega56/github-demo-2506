package com.example.demo.config;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class DeploymentInfo {
    private final String deployedAt;

    public DeploymentInfo() {
        this.deployedAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
            .withZone(ZoneId.of("Asia/Tokyo"))
            .format(Instant.now());
    }

    public String getDeployedAt() {
        return deployedAt;
    }
}
