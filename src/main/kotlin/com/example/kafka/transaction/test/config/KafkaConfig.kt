package com.example.kafka.transaction.test.config

import com.example.kafka.transaction.test.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.DefaultErrorHandler

@EnableKafka
@Configuration
class KafkaConfig(
    @Value("\${spring.kafka.producer.transaction-id-prefix}") private val transactionIdPrefix: String,
    @Value("\${kafka.listeners-count}") private val listenersCount: Int
) {

    @Bean
    fun producerFactory(kafkaProperties: KafkaProperties): ProducerFactory<String, Message>? {
        val props = kafkaProperties.buildProducerProperties()
        val pf = DefaultKafkaProducerFactory<String, Message>(props)
        pf.setTransactionIdPrefix(transactionIdPrefix)
        return pf
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, Message>): KafkaTemplate<String, Message> {
        return KafkaTemplate(producerFactory)
    }

    @Bean
    fun consumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, String> {
        val props = kafkaProperties.buildConsumerProperties()
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, String>): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.setConcurrency(listenersCount)
        factory.consumerFactory = consumerFactory
        factory.setCommonErrorHandler(DefaultErrorHandler())
        return factory
    }

    // Typed consumer:

    // @Bean
    // fun consumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, Message> {
    //     val props = kafkaProperties.buildConsumerProperties()
    //     return DefaultKafkaConsumerFactory<String, Message>(props)
    // }
    //
    // @Bean
    // fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, Message>): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Message>> {
    //     val factory = ConcurrentKafkaListenerContainerFactory<String, Message>()
    //     factory.setConcurrency(listenersCount)
    //     factory.consumerFactory = consumerFactory
    //     factory.setCommonErrorHandler(DefaultErrorHandler())
    //     return factory
    // }
}
