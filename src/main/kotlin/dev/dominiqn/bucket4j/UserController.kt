package dev.dominiqn.bucket4j

import io.github.bucket4j.Bucket
import io.github.bucket4j.SimpleBucketListener
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.Duration

@RestController
@RequestMapping
class UserController {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val statsListeners = SimpleBucketListener()

    private val bucket = Bucket.builder()
        .addLimit { limit ->
            limit.capacity(10)
                .refillIntervally(2, Duration.ofSeconds(10))
        }
        .withListener(statsListeners)
        .build()

    @GetMapping("/stats")
    fun getStats(): ResponseEntity<BucketStats> {
        val stats = BucketStats(
            availableTokens = bucket.availableTokens,
            consumed = statsListeners.consumed,
            rejected = statsListeners.rejected,
            delayedNanos = statsListeners.delayedNanos,
            parkedNanos = statsListeners.parkedNanos,
            interrupted = statsListeners.interrupted,
        )
        return ResponseEntity.ok(stats)
    }


    @GetMapping("/rate-limits")
    fun getRateLimits(): ResponseEntity<String> {
        val rateLimit = bucket.availableTokens
        logger.info("Rate limit: {}", rateLimit)
        return ResponseEntity.ok("Rate limit: $rateLimit")
    }

    @PostMapping("/users")
    fun createUser(
        @RequestBody user: User
    ): ResponseEntity<Unit> {
        if (!bucket.tryConsume(1)) {
            logger.warn("Rate limit exceeded")
            return ResponseEntity.status(429).build()
        }

        logger.info("User created: {}", user)
        return ResponseEntity.created(URI.create("about:blank")).build()
    }
}
