package com.langchain.ollama.controller;

import com.langchain.ollama.services.LlamaAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
public class AIController {

    private LlamaAiService aiService;
    public AIController(LlamaAiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping(path = "/api/v1/generate", produces = "text/event-stream")
    public ResponseEntity<SseEmitter> generate(@RequestParam(value = "prompt") String promptMessage) {
        SseEmitter emitter = new SseEmitter(60000L);
        aiService.generateResult(promptMessage, emitter);
        return ResponseEntity.ok(emitter);
    }
}
