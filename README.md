# class-redis-streams

Repository to showcase how to use Redis Streams in a Spring Boot app. Also publishes a small helper library for defining
consumers

## Structure
- `/demo`: Spring Boot App to produce stream events and consume them
- `/lib`: Helper library to use product and consume from/to Redis Streams.


## Demo Contents
- `config/RedisConfiguration.kt`: sets up the Redis configuration (host and port)
- `consumer/ProductCreatedConsumer.kt`: example implementation of the RedisStreamConsumer interface
- `producer/ProductCreatedProducer.kt`: example implementation of the RedisStreamProducer interface
- `route/StreamTestRoute.kt`: entrypoint route to publish to the stream

## Demo configuration

See [demo/src/main/resources/application.yml](https://github.com/austral-ingsis/class-redis-streams/blob/main/demo/src/main/resources/application.yml) to see how to setup this app

## Using the helper library

To use the helper library use the following:

```groovy

implementation 'org.austral.ingsis:redis-streams:0.1.x'
```

**Note**: this library doesn't declare the version of the dependencies it uses.
