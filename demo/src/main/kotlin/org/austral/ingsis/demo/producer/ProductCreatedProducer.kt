package org.austral.ingsis.demo.producer

import kotlinx.coroutines.reactor.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.austral.ingsis.demo.consumer.ProductCreated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

interface ProductCreatedProducer {
    suspend fun publishEvent(name: String)
}

@Component
class RedisProductCreatedProducer @Autowired constructor(
    @Value("\${stream.key}") streamKey: String,
    redis: ReactiveRedisTemplate<String, String>
) : ProductCreatedProducer, RedisStreamProducer(streamKey, redis) {

    override suspend fun publishEvent(name: String) {
        println("Publishing on stream: $streamKey")
        val product = ProductCreated(name)
        emit(product).awaitSingle()
    }
}