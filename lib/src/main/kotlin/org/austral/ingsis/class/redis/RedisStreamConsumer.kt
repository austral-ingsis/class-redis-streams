package org.austral.ingsis.`class`.redis

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.InetAddress

abstract class RedisStreamConsumer<Value>(protected val streamKey: String, protected val groupId: String, private val redis: ReactiveRedisTemplate<String, String>) {

    protected abstract fun onMessage(record: ObjectRecord<String, Value>)
    private lateinit var flow: Flux<ObjectRecord<String, Value>>

    protected abstract fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, Value>>

    @PostConstruct
    fun subscription() {
        val options = options()

        // Spring doesn't interact very well with reactivity in PostConstruct methods, it makes sense.
        runBlocking {
            try {
                val consumerGroupExists = consumerGroupExists(streamKey, groupId).awaitSingle()
                if(!consumerGroupExists) {
                    println("Consumer group ${groupId} for stream ${streamKey} doesn't exist. Creating...")
                    createConsumerGroup(streamKey, groupId).awaitSingle()
                }else {
                    println("Consumer group ${groupId} for stream ${streamKey} exists!")
                }
            } catch (e: Exception) {
                println("Stream ${streamKey} doesn't exist. Creating stream ${streamKey} and group ${groupId}")
                redis.opsForStream<Any, Any>().createGroup(streamKey, groupId).awaitSingle()
                println("Exception: $e")
            }
        }
        val container = StreamReceiver.create(redis.connectionFactory, options)
        flow = container.receiveAutoAck(
            Consumer.from(groupId, InetAddress.getLocalHost().hostName),
            StreamOffset.create(streamKey, ReadOffset.lastConsumed())
        )
        flow.subscribe(this::onMessage)
    }

    private fun createConsumerGroup(streamKey: String, groupId: String): Mono<String> {
        return redis.opsForStream<Any, Any>().createGroup(streamKey, groupId)
    }

    private fun consumerGroupExists(stream: String, group: String): Mono<Boolean> {
        return redis.opsForStream<Any, Any>().groups(stream).any { it.groupName() == group }
    }
}