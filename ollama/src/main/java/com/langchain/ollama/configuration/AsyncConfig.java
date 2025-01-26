package com.langchain.ollama.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EqualsAndHashCode(callSuper = false)
@Data
@Configuration
@EnableAsync
@ConfigurationProperties("async-config")
public class AsyncConfig extends AsyncConfigurerSupport {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;

    private final BeanFactory beanFactory;

    public AsyncConfig(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("ollama-async");
        executor.initialize();

        return new LazyTraceExecutor(this.beanFactory, executor);
    }
}
