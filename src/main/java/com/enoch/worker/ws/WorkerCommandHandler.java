package com.enoch.worker.ws;

import com.enoch.worker.dto.CommandMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WorkerCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(WorkerCommandHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handle(CommandMessage cmd) {
        if (cmd == null) {
            log.warn("Received null command message");
            return;
        }

        log.info("Received message: type={}, from={}, to={}, payload={}",
                cmd.getType(), cmd.getFrom(), cmd.getTo(), cmd.getPayload());

        switch (cmd.getType()) {
            case "request:register" -> handleRegister(cmd);
            case "request:ping", "PING" -> ping();
            case "request:reload", "RELOAD" -> reload();
            case "request:stop", "STOP" -> stop();
            case "request:execute", "EXECUTE" -> execute(cmd.getPayload());
            case "request:heartbeat" -> heartbeat(cmd);
            default -> log.warn("Unknown command: {}", cmd.getType());
        }
    }

    public String createAcknowledgement(CommandMessage cmd, String rawMessage) {
        if (cmd == null) {
            return null;
        }

        Map<String, Object> responsePayload = new HashMap<>();
        responsePayload.put("received", true);
        responsePayload.put("message", "Poprawnie otrzymano wiadomość");
        responsePayload.put("rawMessage", rawMessage == null ? "" : rawMessage);

        CommandMessage response = new CommandMessage();
        response.setId(cmd.getId() != null ? cmd.getId() : java.util.UUID.randomUUID().toString());
        response.setType("response:message_received");
        response.setFrom("worker");
        response.setTo(cmd.getFrom() == null ? "conductor" : cmd.getFrom());
        response.setTimestamp(System.currentTimeMillis());
        response.setStatus(200);
        response.setPayload(responsePayload);

        try {
            String serialized = objectMapper.writeValueAsString(response);
            log.info("Prepared acknowledgement: {}", serialized);
            return serialized;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize acknowledgement", e);
            return null;
        }
    }

    private void handleRegister(CommandMessage cmd) {
        log.info("Registration request received: {}", cmd.getPayload());
    }

    private void ping() {
        log.info("PING received");
    }

    private void reload() {
        log.info("Reload configuration");
    }

    private void stop() {
        log.info("Worker stopping");
        System.exit(0);
    }

    private void execute(Object payload) {
        String payloadText = payload == null ? "null" : payload.toString();
        log.info("Executing: {}", payloadText);
    }

    private void heartbeat(CommandMessage cmd) {
        Map<String, Object> responsePayload = new HashMap<>();
        responsePayload.put("received", true);
        responsePayload.put("message", "Poprawnie otrzymano wiadomość");
        responsePayload.put("rawMessage", cmd.getPayload());

        CommandMessage response = new CommandMessage();
        response.setId(cmd.getId());
        response.setType("response:heartbeat");
        response.setFrom("worker");
        response.setTo(cmd.getFrom());
        response.setTimestamp(System.currentTimeMillis());
        response.setStatus(200);
        response.setPayload(responsePayload);

        try {
            log.info("Sending heartbeat response: {}", objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize heartbeat response", e);
        }
    }
}