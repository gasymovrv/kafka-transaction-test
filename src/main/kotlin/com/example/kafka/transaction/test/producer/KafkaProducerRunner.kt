package com.example.kafka.transaction.test.producer

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
@ConditionalOnProperty(
    prefix = "kafka",
    name = ["producer-enabled"],
    havingValue = "true"
)
class KafkaProducerRunner(
    private val kafkaProducerService: KafkaProducerService
) : CommandLineRunner {

    private var count = AtomicInteger(0)

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String?) {
        for (i in 0..5) {
            sendSuccessfully()
            sendWithError()
        }
    }

    private fun sendSuccessfully() {
        kafkaProducerService.generateAndSendPackage("tx${count.incrementAndGet()}", false)
    }

    private fun sendWithError() {
        try {
            kafkaProducerService.generateAndSendPackage("tx${count.incrementAndGet()}-err", true)
        } catch (e: Exception) {
            log.error(e.message)
        }
    }
}
