package com.mvv.bank.orders.app

import com.mvv.bank.orders.app.config.AppConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import


@SpringBootApplication
@Import(AppConfig::class)
class OrderApp

@SuppressWarnings("resource")
fun main(args: Array<String>) {
    SpringApplication.run(OrderApp::class.java, *args)

    // strange error/warning is shown:
    // Cannot inline bytecode built with JVM target 17 into bytecode that is being built with JVM target 12.
    //org.springframework.boot.runApplication<OrderApp>(*args)
}
