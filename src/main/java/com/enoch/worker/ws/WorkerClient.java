package com.enoch.worker.ws;

import com.enoch.worker.dto.CommandMessage;
import com.enoch.worker.dto.RegisterMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class WorkerClient extends WebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(WorkerClient.class);

    private final String instanceId;

    private final ObjectMapper mapper = new ObjectMapper();

    private final WorkerCommandHandler commandHandler;

    public WorkerClient(
            String url,
            String instanceId,
            WorkerCommandHandler commandHandler
    ) throws Exception {

        super(new URI(url));

        this.instanceId = instanceId;
        this.commandHandler = commandHandler;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {

        try {

            send(
                    mapper.writeValueAsString(
                            new RegisterMessage(instanceId)
                    )
            );

            log.info("Connected to manager.");

        } catch (Exception e) {
            log.error("Failed to register worker on open", e);
        }
    }

    @Override
    public void onMessage(String message) {

        try {
            log.info("Received message from manager: {}", message);

            CommandMessage command =
                    mapper.readValue(message, CommandMessage.class);

            commandHandler.handle(command);

            String ack = commandHandler.createAcknowledgement(command, message);
            if (ack != null) {
                send(ack);
                log.info("Sent acknowledgement: {}", ack);
            }

        } catch (Exception e) {
            log.error("Failed to handle incoming websocket message: {}", message, e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

        log.info("Disconnected from manager. code={}, reason={}, remote={}", code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {

        log.error("WebSocket client error", ex);
    }
}