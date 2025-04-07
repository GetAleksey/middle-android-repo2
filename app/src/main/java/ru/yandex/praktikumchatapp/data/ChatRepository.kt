package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import kotlin.math.pow
import kotlin.random.Random

private const val INITIAL_DELAY = 200L
private const val DELAY_FACTOR = 1.5
private const val MAX_DELAY = 5000L
private const val JITTER = 0.1

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {

    fun getReplyMessage(): Flow<String> {
        return api.getReply()
            .retryWhen { _, attempt ->
                delay(timeMillis = calculateDelay(attempt.toInt()))
                true
            }
    }

    private fun calculateDelay(attempt: Int): Long {
        val baseDelay = (INITIAL_DELAY * DELAY_FACTOR.pow(attempt)).toLong().coerceAtMost(MAX_DELAY)
        val jitterFactor = (1 + Random.nextDouble(-JITTER, JITTER))
        return (baseDelay * jitterFactor).toLong()
    }
}