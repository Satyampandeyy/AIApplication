package com.langchain.ollama.controller;

import com.langchain.ollama.services.LlamaAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIController {

    @Autowired
    private LlamaAiService aiService;

    @GetMapping("/api/v1/generate")
    public String generate(@RequestParam(value = "prompt") String promptMessage) {
        return aiService.generateResult(promptMessage);
    }
}
