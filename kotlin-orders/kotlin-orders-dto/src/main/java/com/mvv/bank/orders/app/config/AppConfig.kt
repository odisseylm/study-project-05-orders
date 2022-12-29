package com.mvv.bank.orders.app.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement


@ComponentScan(basePackages = ["com.mvv.bank.orders.service"])
@EnableJpaRepositories("com.mvv.bank.orders.repository.jpa")
@EntityScan("com.mvv.bank.orders.repository.jpa")
@EnableTransactionManagement
class AppConfig {
}
