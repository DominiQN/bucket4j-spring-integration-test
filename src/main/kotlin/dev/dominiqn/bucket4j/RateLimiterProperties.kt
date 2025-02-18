package dev.dominiqn.bucket4j

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import java.time.Instant

@ConfigurationProperties("rate-limiter")
class RateLimiterProperties(
    val capacity: Long,
    val refillPolicy: RefillPolicy,
    val refillTokens: Long,
    val refillPeriod: Duration,
    val timeOfFirstRefill: Instant? = null,
) {
    enum class RefillPolicy {
        GREEDY,
        INTERVAL,
        INTERVAL_ALIGNED,
        INTERVAL_ALIGNED_WITH_ADAPTIVE_INITIAL_TOKENS,
    }
}
