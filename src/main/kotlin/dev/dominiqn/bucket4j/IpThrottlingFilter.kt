package dev.dominiqn.bucket4j

import io.github.bucket4j.Bucket
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class IpThrottlingFilter(
    private val bucketFactory: BucketFactory,
) : Filter {
    private val buckets = ConcurrentHashMap<String, Bucket>()

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain?
    ) {
        val httpServletRequest = request as HttpServletRequest
        val ip = httpServletRequest.remoteAddr

        val bucket = getBucket(ip)

        val consumptionProbe = bucket.tryConsumeAndReturnRemaining(1)
        if (consumptionProbe.isConsumed) {
            chain?.doFilter(request, response)
        } else {
            val httpServletResponse = response as HttpServletResponse
            httpServletResponse.sendError(429, "Too Many Requests")
            httpServletResponse.contentType = "application/json"
            httpServletResponse.characterEncoding = "UTF-8"
            httpServletResponse.writer.write("""{"message": "Rate limit exceeded"}""")
            httpServletResponse.addHeader("X-RateLimit-Limit", bucket.availableTokens.toString())
            httpServletResponse.addHeader("X-RateLimit-Remaining", consumptionProbe.remainingTokens.toString())
            httpServletResponse.addHeader("X-RateLimit-Reset", consumptionProbe.nanosToWaitForRefill.toString())
            httpServletResponse.addHeader("Retry-After", (consumptionProbe.nanosToWaitForRefill / 1_000_000_000).toString())
        }
    }

    private fun getBucket(ip: String): Bucket {
        return buckets.computeIfAbsent(ip) { bucketFactory.createBucket() }
    }
}
