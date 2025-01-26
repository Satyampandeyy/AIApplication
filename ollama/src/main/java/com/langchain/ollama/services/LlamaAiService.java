package com.langchain.ollama.services;

import com.langchain.ollama.models.ResponseEmitter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class LlamaAiService {
    private final OllamaChatModel chatModel;

    private final OllamaOptions ollamaConfig;

    public LlamaAiService(OllamaChatModel chatModel, @Qualifier("LlamaAI") OllamaOptions ollamaConfig) {
        this.chatModel = chatModel;
        this.ollamaConfig = ollamaConfig;
    }
    public void generateResult(String prompt, SseEmitter emitter) {

        System.out.println("Entered generateResult");
        ResponseEmitter responseEmitter = new ResponseEmitter(emitter);
        Flux<ChatResponse> response = chatModel.stream(
                new Prompt(
                        prompt,
                        ollamaConfig
                ));
        try {
            response.subscribe(
                    data -> {
                        data.getResults().forEach(res -> {
                            String text = res.getOutput().getContent();
                            responseEmitter.sendData(text);
                        });
                        },
                    error -> {
                        responseEmitter.sendError(error.getMessage());
                    }, () -> {
                        responseEmitter.complete();
                    });

        } catch (Exception e) {
            responseEmitter.sendError(e.getMessage());
        }
    }
}
