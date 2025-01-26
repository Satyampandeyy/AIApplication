package com.langchain.ollama.models;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ResponseEmitter {
    private static final String TYPE_RESULT = "result";
    private static final String TYPE_ERROR = "error";
    private static final String TYPE_COMPLETE = "complete";

    private final SseEmitter emitter;
    private final List<String> responses = new ArrayList<>();

    private boolean completed = false;

    public ResponseEmitter(SseEmitter emitter) {
        this.emitter = emitter;

        emitter.onTimeout(this::timeoutHandler);
        emitter.onCompletion(this::completionHandler);
    }

    public synchronized void sendData(String message) {
        sendData(message, TYPE_RESULT);
    }

    public synchronized void sendError(String error) {
        try {

            sendData(error, TYPE_ERROR);
        } catch (Exception ex) {
            log.error("Failed to send error response SSE event to client.", ex);
        }
    }

    public synchronized void complete() {
        try {

            sendData("", TYPE_COMPLETE);
            emitter.complete();
            this.completed = true;
        } catch (Exception e) {
            log.error("Failed to complete SSE emitter.", e);
        }
    }

    private synchronized void sendData(String message, String type) {
        if (completed) {
            log.warn("Attempted to send message after completion. Will not attempt to send the message with type {}", type);
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                .name(type)
                .data(message));
            System.out.println("Sent message: " + message);
            responses.add(message);
        } catch (Exception e) {
            log.error("Failed to send SSE event to client.", e);
        }
    }

    private synchronized void timeoutHandler() {
        log.error("SSE emitter timed out.");
        completed = true;
    }

    private synchronized void completionHandler() {
        log.debug("SSE emitter completed.");
        completed = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseEmitter that = (ResponseEmitter) o;
        return Objects.equals(emitter, that.emitter);
    }
}
