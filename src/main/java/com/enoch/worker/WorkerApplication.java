package com.enoch.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkerApplication {

    private static final Logger log = LoggerFactory.getLogger(WorkerApplication.class);

    public static void main(String[] args) {
        String instanceName = resolveInstanceName(args);
        Thread.currentThread().setName(instanceName);

        log.info("Starting {} using Java {} with PID {}", instanceName, Runtime.version().feature(), ProcessHandle.current().pid());

        SpringApplication.run(WorkerApplication.class, args);
    }

    private static String resolveInstanceName(String[] args) {
        if (args != null) {
            for (String arg : args) {
                if (arg.startsWith("--instanceId=")) {
                    return arg.substring("--instanceId=".length());
                }
                if (arg.startsWith("--instance-id=")) {
                    return arg.substring("--instance-id=".length());
                }
                if (arg.startsWith("--instanceName=")) {
                    return arg.substring("--instanceName=".length());
                }
            }
        }

        String envName = System.getenv("INSTANCE_ID");
        if (envName != null && !envName.isBlank()) {
            return envName;
        }

        String propertyName = System.getProperty("instance.id");
        if (propertyName != null && !propertyName.isBlank()) {
            return propertyName;
        }

        return "no-name";
    }
}
