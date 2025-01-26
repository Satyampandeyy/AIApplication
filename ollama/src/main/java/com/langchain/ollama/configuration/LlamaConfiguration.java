package com.langchain.ollama.configuration;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlamaConfiguration {

    @Bean
    public OllamaOptions LlamaAI() {
        System.out.println("Llama config got called");

       return OllamaOptions.builder()
               .model("llama3")
               .temperature(0.4)
               .build();
    }
}
