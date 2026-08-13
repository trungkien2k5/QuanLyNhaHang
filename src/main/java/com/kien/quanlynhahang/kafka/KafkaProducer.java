package com.kien.quanlynhahang.kafka;

import com.kien.quanlynhahang.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private static final String TOPIC = "order.created";
    public void sendOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.maHD().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("KAFKA GỬI THẤT BẠI", ex);
                    } else {
                        log.info(
                                "KAFKA GỬI THÀNH CÔNG - topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    }
                });
    }
}