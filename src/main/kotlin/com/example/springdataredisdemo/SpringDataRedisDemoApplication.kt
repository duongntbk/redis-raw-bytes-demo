package com.example.springdataredisdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.core.RedisTemplate
import java.nio.ByteBuffer

@SpringBootApplication
class SpringDataRedisDemoApplication

fun main(args: Array<String>) {
	val context = runApplication<SpringDataRedisDemoApplication>(*args)
	val redisTemplateSerialize = context.getBean(
		"redisTemplateSerialize", RedisTemplate::class.java
	) as RedisTemplate<Long, Double>
	val redisTemplateByteArray = context.getBean(
		"redisTemplateByteArray", RedisTemplate::class.java
	) as RedisTemplate<ByteArray, ByteArray>

	setBySerialize(redisTemplateSerialize)
	setByBytes(redisTemplateByteArray)
	opsSetByBytes(redisTemplateByteArray)

	println(getValueSetByBytes(redisTemplateByteArray))
	println(opsGetValueByBytes(redisTemplateByteArray))
}

private fun setBySerialize(
	redisTemplate: RedisTemplate<Long, Double>,
) = redisTemplate.opsForValue().multiSet(
	mapOf(
		6359284517L to 0.5238106733071787,
		Long.MAX_VALUE to 0.6238106733071787,
	)
)

private fun setByBytes(
	redisTemplate: RedisTemplate<ByteArray, ByteArray>,
) = redisTemplate.execute { connect ->
	connect.stringCommands().set(
		6359284516L.toByteArray(),
		0.4238106733071787.toByteArray(),
	)
}

private fun opsSetByBytes(redisTemplate: RedisTemplate<ByteArray, ByteArray>) =
	redisTemplate.opsForValue().multiSet(
		mapOf(
			Long.MAX_VALUE.toByteArray() to  0.6238106733071787.toByteArray(),
			6359284517L.toByteArray() to 0.5238106733071787.toByteArray(),
		)
	)

private fun getValueSetByBytes(
	redisTemplate: RedisTemplate<ByteArray, ByteArray>,
): List<Double?> = redisTemplate.execute { connection ->
	val nums = listOf(6359284516L, 6359284517L, Long.MAX_VALUE)
	val keys = Array(nums.size) { nums[it].toByteArray() }
	val a1 = connection.stringCommands().mGet(*keys)
	a1
}?.map { it?.toDouble() }!!

private fun opsGetValueByBytes(
	redisTemplate: RedisTemplate<ByteArray, ByteArray>,
): List<Double?> = redisTemplate.opsForValue().multiGet(
	listOf(6359284516L.toByteArray(), 6359284517L.toByteArray(), Long.MAX_VALUE.toByteArray())
)?.map { it?.toDouble() }!!

private fun Long.toByteArray(): ByteArray {
	val result = ByteArray(8)
	for (i in 0..7) {
		result[i] = (this shr (8 * i) and 0xFF).toByte()
	}
	return result
}

private fun Double.toByteArray(): ByteArray = ByteBuffer.allocate(8).putDouble(this).array()

private fun ByteArray.toDouble(): Double = ByteBuffer.wrap(this).double