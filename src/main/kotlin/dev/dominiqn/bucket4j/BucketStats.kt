package dev.dominiqn.bucket4j

data class BucketStats(
    val availableTokens: Long,
    val consumed: Long,
    val rejected: Long,
    val delayedNanos: Long,
    val parkedNanos: Long,
    val interrupted: Long,
)
