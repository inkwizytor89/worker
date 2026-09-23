package com.enoch.worker.bootstrap;

import com.enoch.worker.config.WorkerProperties;
import com.enoch.worker.ws.WorkerClient;
import com.enoch.worker.ws.WorkerCommandHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class WorkerBootstrap
        implements CommandLineRunner {

    private final WorkerProperties properties;
    private final WorkerCommandHandler handler;

    public WorkerBootstrap(
            WorkerProperties properties,
            WorkerCommandHandler handler
    ) {

        this.properties = properties;
        this.handler = handler;
    }

    @Override
    public void run(String... args)
            throws Exception {

        WorkerClient client =
                new WorkerClient(
                        properties.getManagerUrl(),
                        properties.getInstanceId(),
                        handler
                );

        client.connectBlocking();
    }
}