package dev.dominiqn.bucket4j

import io.github.bucket4j.BandwidthBuilder
import io.github.bucket4j.Bucket

class BucketFactory(
    private val properties: RateLimiterProperties,
) {
    fun createBucket(): Bucket {
        return Bucket.builder()
            .addLimit { limit ->
                limit.capacity(properties.capacity)
                    .withPolicy(properties.refillPolicy)
            }
            .build()
    }

    private fun BandwidthBuilder.BandwidthBuilderRefillStage.withPolicy(
        refillPolicy: RateLimiterProperties.RefillPolicy,
    ): BandwidthBuilder.BandwidthBuilderBuildStage {
        return when (refillPolicy) {
            RateLimiterProperties.RefillPolicy.GREEDY ->
                refillIntervally(
                    properties.refillTokens,
                    properties.refillPeriod,
                )

            RateLimiterProperties.RefillPolicy.INTERVAL ->
                refillIntervally(
                    properties.refillTokens,
                    properties.refillPeriod,
                )

            RateLimiterProperties.RefillPolicy.INTERVAL_ALIGNED ->
                refillIntervallyAligned(
                    properties.refillTokens,
                    properties.refillPeriod,
                    requireNotNull(properties.timeOfFirstRefill) {
                        "timeOfFirstRefill is required for INTERVAL_ALIGNED refill policy"
                    },
                )

            RateLimiterProperties.RefillPolicy.INTERVAL_ALIGNED_WITH_ADAPTIVE_INITIAL_TOKENS ->
                refillIntervallyAlignedWithAdaptiveInitialTokens(
                    properties.refillTokens,
                    properties.refillPeriod,
                    requireNotNull(properties.timeOfFirstRefill) {
                        "timeOfFirstRefill is required for INTERVAL_ALIGNED_WITH_ADAPTIVE_INITIAL_TOKENS refill policy"
                    },
                )
        }
    }
}
