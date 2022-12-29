package com.example.kafka.transaction.test.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "kafka",
    name = ["consumer-enabled"],
    havingValue = "true"
)
class KafkaConsumerService {

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["\${kafka.topic}"], autoStartup = "true")
    fun listen(record: ConsumerRecord<String, String>) {
        log.info(
            "### Thread_${Thread.currentThread().id} got record from kafka: " +
                "key = ${record.key()}, " +
                "value = ${record.value()}"
        )
    }
}
