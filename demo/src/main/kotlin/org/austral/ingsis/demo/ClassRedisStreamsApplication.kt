package org.austral.ingsis.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ClassRedisStreamsApplication

fun main(args: Array<String>) {
    runApplication<ClassRedisStreamsApplication>(*args)
}
