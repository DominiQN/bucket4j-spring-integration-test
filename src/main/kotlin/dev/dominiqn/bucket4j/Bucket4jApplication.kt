package dev.dominiqn.bucket4j

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class Bucket4jApplication

fun main(args: Array<String>) {
    runApplication<Bucket4jApplication>(*args)
}
