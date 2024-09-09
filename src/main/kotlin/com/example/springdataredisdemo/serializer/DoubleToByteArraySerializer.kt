package com.example.springdataredisdemo.serializer

import org.springframework.data.redis.serializer.RedisSerializer
import java.nio.ByteBuffer

class DoubleToByteArraySerializer: RedisSerializer<Double> {
    override fun serialize(value: Double?): ByteArray? = value?.toByteArray()

    override fun deserialize(bytes: ByteArray?): Double? = bytes?.toDouble()
}

fun Double.toByteArray(): ByteArray = ByteBuffer.allocate(8).putDouble(this).array()

fun ByteArray?.toDouble(): Double = ByteBuffer.wrap(this).double
