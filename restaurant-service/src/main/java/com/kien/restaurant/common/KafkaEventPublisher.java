package com.kien.restaurant.common;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Retry(name = "kafkaPublisher")
    @CircuitBreaker(name = "kafkaPublisher", fallbackMethod = "publishFallback")
    @TimeLimiter(name = "kafkaPublisher")
    public CompletableFuture<Void> publish(String topic, String key, Object event) {
        return kafkaTemplate.send(topic, key, event)
                .thenAccept(result -> log.info("Kafka event published topic={} key={}", topic, key));
    }

    private CompletableFuture<Void> publishFallback(String topic, String key, Object event, Throwable throwable) {
        log.error("Kafka publish failed topic={} key={}", topic, key, throwable);
        return CompletableFuture.failedFuture(throwable);
    }
}
