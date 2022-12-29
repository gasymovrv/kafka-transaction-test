package com.example.kafka.transaction.test.producer

import com.example.kafka.transaction.test.Message
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnProperty(
    prefix = "kafka",
    name = ["producer-enabled"],
    havingValue = "true"
)
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, Message>,
    @Value("\${kafka.topic}") private val topic: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun generateAndSendPackage(txName: String, error: Boolean) {
        log.info("=========================== tx $txName started ====================")
        for (i in 0..5) {
            val message = Message(i, "$txName#$i")

            kafkaTemplate.send(topic, message.id.toString(), message)
                .addCallback(this::successCallback, this::failureCallback)

            if (error && i > 3) throw RuntimeException("Test error to interrupt transaction")
        }
        log.info("=========================== tx $txName finished ====================")
    }

    private fun successCallback(sendResult: SendResult<String, Message>?) {
        log.info(
            "#KafkaProducerService: sent message [key={}, value={}]",
            sendResult?.producerRecord?.key(),
            sendResult?.producerRecord?.value()
        )
    }

    private fun failureCallback(throwable: Throwable) {
        log.error("#KafkaProducerService: cannot send message to kafka: ${throwable.message}")
    }
}
