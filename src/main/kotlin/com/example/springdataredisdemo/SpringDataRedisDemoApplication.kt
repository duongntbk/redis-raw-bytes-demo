package com.example.springdataredisdemo

import com.example.springdataredisdemo.serializer.toByteArray
import com.example.springdataredisdemo.serializer.toDouble
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStringCommands
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.types.Expiration
import java.time.Duration

@SpringBootApplication
class SpringDataRedisDemoApplication

fun main(args: Array<String>) {
	val context = runApplication<SpringDataRedisDemoApplication>(*args)
	val redisTemplateSerialize = context.getBean(
		"redisTemplateSerialize", RedisTemplate::class.java
	) as RedisTemplate<Long, Double>
	val redisTemplateByteArray = context.getBean(
		"redisTemplateByteArray", RedisTemplate::class.java
	) as RedisTemplate<Long, Double>
	val redisConnectionFactory = context.getBean(RedisConnectionFactory::class.java)

	setByOps(redisTemplateSerialize)
	setByOps(redisTemplateByteArray)
	setByBytes(redisTemplateByteArray)

	println(getByOps(redisTemplateSerialize))
	println(getByOps(redisTemplateByteArray))

	setByConnection(redisConnectionFactory)
	println(getByConnection(redisConnectionFactory))

	redisTemplateSerialize.opsForValue().set(1L, 1.0)
	redisTemplateByteArray.opsForValue().set(1L, 1.0)
}

private fun setByOps(
	redisTemplate: RedisTemplate<Long, Double>,
) = redisTemplate.opsForValue().multiSet(
	mapOf(
		6359284517L to 0.5238106733071787,
		Long.MAX_VALUE to 0.6238106733071787,
	)
)

private fun setByBytes(
	redisTemplate: RedisTemplate<Long, Double>,
) = redisTemplate.executePipelined { conn ->
	conn.stringCommands().set(
		6359284516L.toByteArray(),
		0.4238106733071787.toByteArray(),
		Expiration.from(Duration.ofSeconds(3600)),
		RedisStringCommands.SetOption.UPSERT,
	)

	conn.stringCommands().set(
		6359284515L.toByteArray(),
		0.4238106733071787.toByteArray(),
		Expiration.from(Duration.ofSeconds(3600)),
		RedisStringCommands.SetOption.UPSERT,
	)
}

private fun getByOps(
	redisTemplate: RedisTemplate<Long, Double>,
): List<Double?> = redisTemplate.opsForValue().multiGet(
	listOf(6359284515L, 6359284516L, 6359284517L, Long.MAX_VALUE)
)!!


fun setByConnection(connectionFactory: RedisConnectionFactory) {
	val conn = connectionFactory.connection

	conn.openPipeline()
	conn.stringCommands().set(
		100L.toByteArray(),
		0.1.toByteArray(),
		Expiration.from(Duration.ofSeconds(3600)),
		RedisStringCommands.SetOption.UPSERT,
	)

	conn.stringCommands().set(
		101L.toByteArray(),
		0.101.toByteArray(),
		Expiration.from(Duration.ofSeconds(3600)),
		RedisStringCommands.SetOption.UPSERT,
	)
	conn.closePipeline()
}

fun getByConnection(connectionFactory: RedisConnectionFactory): List<Double?> {
	val conn = connectionFactory.connection
	val nums = listOf(100L, 101L)
	val keys = Array(nums.size) { nums[it].toByteArray() }
	return conn.stringCommands().mGet(*keys)!!.map { it?.toDouble() }
}
