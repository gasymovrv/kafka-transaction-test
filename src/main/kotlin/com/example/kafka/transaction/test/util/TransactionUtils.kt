package com.example.kafka.transaction.test.util

import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

object TransactionUtils {

    /**
     * Use only inside @Transactional methods
     * <p>
     * Executes an [action] right before commit of a parent transaction
     * to synchronize different transaction managers by ordering
     * <p>
     * E.g. Kafka transaction is in the [action] and MongoDB transaction is in the parent transaction
     *
     * <p><b>Example:</b>
     * <blockquote><pre>{@code
     @Transactional
     fun createAll(list: List<Entity>): List<Entity> {
        val saved = repository.saveAll(list)
        doBeforeCommit {
            kafkaTemplate.executeInTransaction { kafkaOperations ->
                saved.forEach { entity ->
                    kafkaOperations.send(topic, entity.id, entity)
                }
            }
        }
        return saved
     }
     * }</pre></blockquote>
     *
     * @param action action that performs in a transaction manager other than the parent transaction
     */
    fun doBeforeCommit(action: () -> Unit) {
        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun beforeCommit(readOnly: Boolean) {
                    action()
                }
            }
        )
    }
}
