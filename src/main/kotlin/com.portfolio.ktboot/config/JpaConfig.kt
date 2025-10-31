package com.portfolio.ktboot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import java.util.*
import javax.sql.DataSource

@Configuration
class JpaConfig {
    @Bean
    fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        em.setPackagesToScan("com.portfolio.ktboot")

        val vendorAdapter = HibernateJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter

        val properties = Properties()
        properties["hibernate.dialect"] = "org.hibernate.dialect.MariaDBDialect"
        properties["hibernate.show_sql"] = "true"
        properties["hibernate.format_sql"] = "true"
        properties["hibernate.hbm2ddl.auto"] = "update"

        em.setJpaProperties(properties)

        return em
    }
}