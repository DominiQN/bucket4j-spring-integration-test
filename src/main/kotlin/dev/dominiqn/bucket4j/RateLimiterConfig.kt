package dev.dominiqn.bucket4j

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RateLimiterConfig(
    private val rateLimiterProperties: RateLimiterProperties,
) {
    @Bean
    fun bucketFactory(): BucketFactory {
        return BucketFactory(rateLimiterProperties)
    }
}
