package com.example.springdataredisdemo.config

import com.example.springdataredisdemo.serializer.DoubleToByteArraySerializer
import com.example.springdataredisdemo.serializer.LongToByteArraySerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer
import java.time.Duration

@Configuration
class RedisConfig {
    @Bean
    fun redisTemplateSerialize(
        @Value("\${redis.host}") redisHost: String,
        @Value("\${redis.port}") redisPort: Int,
        @Value("\${redis.password}") redisPassword: String,
    ) : RedisTemplate<Long, Double> {
        val redisConfig = RedisStandaloneConfiguration(redisHost, redisPort)
        redisConfig.setPassword(redisPassword)

        val lettuceClientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(10))
            .build()

        val lettuceConnectionFactory = LettuceConnectionFactory(redisConfig, lettuceClientConfig)
        lettuceConnectionFactory.afterPropertiesSet()

        val redisTemplate = RedisTemplate<Long, Double>()
        redisTemplate.connectionFactory = lettuceConnectionFactory
        redisTemplate.keySerializer = GenericToStringSerializer(Long::class.java)
        redisTemplate.valueSerializer = GenericToStringSerializer(Double::class.java)
        redisTemplate.afterPropertiesSet()

        return redisTemplate
    }

    @Bean
    public fun redisConnectionFactory(
        @Value("\${redis.host}") redisHost: String,
        @Value("\${redis.port}") redisPort: Int,
        @Value("\${redis.password}") redisPassword: String,
    ): RedisConnectionFactory {
        val redisConfig = RedisStandaloneConfiguration(redisHost, redisPort)
        redisConfig.setPassword(redisPassword)

        val lettuceClientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(10))
            .build()

        val lettuceConnectionFactory = LettuceConnectionFactory(redisConfig, lettuceClientConfig)
        lettuceConnectionFactory.afterPropertiesSet()
        return lettuceConnectionFactory
    }

    @Bean
    fun redisTemplateByteArray(
        redisConnectionFactory: RedisConnectionFactory
    ) : RedisTemplate<Long, Double> {
        val redisTemplate = RedisTemplate<Long, Double>()
        redisTemplate.connectionFactory = redisConnectionFactory
        redisTemplate.keySerializer = LongToByteArraySerializer()
        redisTemplate.valueSerializer = DoubleToByteArraySerializer()
        redisTemplate.afterPropertiesSet()

        return redisTemplate
    }
}
