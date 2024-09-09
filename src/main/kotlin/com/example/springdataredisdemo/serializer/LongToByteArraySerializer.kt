package com.example.springdataredisdemo.serializer

import org.springframework.data.redis.serializer.RedisSerializer

class LongToByteArraySerializer : RedisSerializer<Long> {
    override fun serialize(value: Long?): ByteArray? = value?.toByteArray()

    override fun deserialize(bytes: ByteArray?): Long? = bytes?.let {
        var result = 0L
        for (i in 0..7) {
            result = result or ((it[i].toLong() and 0xFF) shl (8 * i))
        }
        result
    }
}

fun Long.toByteArray(): ByteArray {
    val result = ByteArray(8)
    for (i in 0..7) {
        result[i] = (this shr (8 * i) and 0xFF).toByte()
    }
    return result
}
