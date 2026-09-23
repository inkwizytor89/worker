package com.enoch.worker.dto;

import java.util.Map;

public class RegisterMessage {

    private final String id = java.util.UUID.randomUUID().toString();
    private final String type = "request:register";
    private final String from = "worker";
    private final String to = "conductor";
    private final long timestamp = System.currentTimeMillis();
    private final int status = 100;
    private final Map<String, Object> payload;

    public RegisterMessage(String instanceId) {
        this.payload = Map.of("instanceId", instanceId);
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }
}