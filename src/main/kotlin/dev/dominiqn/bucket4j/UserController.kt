package dev.dominiqn.bucket4j

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping
    fun createUser(
        @RequestBody user: User
    ) {
        logger.info("User created: {}", user)
    }
}
