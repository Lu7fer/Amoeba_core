package cf.vbnm.amoeba.config


import cf.vbnm.amoeba.core.spi.Starter
import org.h2.jdbcx.JdbcDataSource
import org.hibernate.dialect.H2Dialect
import org.hibernate.jpa.HibernatePersistenceProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaDialect
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.web.servlet.config.annotation.*
import java.util.*
import javax.sql.DataSource


@Configuration
@ComponentScan("cf.vbnm.amoeba.repository.persistence")
@EnableJpaRepositories(
    "cf.vbnm.amoeba.repository.persistence",
    entityManagerFactoryRef = "persistenceEntityManagerFactory", transactionManagerRef = "persistenceTransactionManager"
)
open class PersistenceDatabaseConfiguration {

    @Bean("persistenceDataSource")
    open fun persistenceDataSource(
        @Value("\${amoeba.jdbc.persistence.url:jdbc:h2:file:D:\\DevelopmentWorkspace/core/Amoeba/db/core}") url: String,
        @Value("\${amoeba.jdbc.persistence.username:amoeba}") username: String,
        @Value("\${amoeba.jdbc.persistence.password:amoeba}") password: String,
    ): DataSource {
        return JdbcDataSource().apply {
            this.user = username
            this.password = password
            this.setUrl(url)
        }
    }

    @Bean("persistenceEntityManagerFactory")
    open fun entityManagerFactory(
        @Qualifier("persistenceDataSource") dataSource: DataSource,
        @Qualifier("persistenceJpaVendorAdapter") jpaVendorAdapter: HibernateJpaVendorAdapter
    ): LocalContainerEntityManagerFactoryBean {
        return LocalContainerEntityManagerFactoryBean().apply {
            this.dataSource = dataSource
            this.jpaVendorAdapter = jpaVendorAdapter
            val list = ArrayList<String>()
            list.add("cf.vbnm.amoeba.entity")
            Starter.findServices().forEach {
                list.addAll(it.getEntityPackages())
            }
            this.setPackagesToScan(*list.toTypedArray())
            persistenceProvider = HibernatePersistenceProvider()
            jpaDialect = HibernateJpaDialect()
        }
    }

    @Bean("persistenceJpaVendorAdapter")
    open fun jpaVendorAdapter(): HibernateJpaVendorAdapter {
        return HibernateJpaVendorAdapter().apply {
            this.setDatabase(Database.H2)
            this.setGenerateDdl(true)
            this.setShowSql(true)
            this.setDatabasePlatform(H2Dialect::class.java.name)
        }
    }

    @Bean("persistenceTransactionManager")
    open fun transactionManager(@Qualifier("persistenceEntityManagerFactory") entityManagerFactoryBean: LocalContainerEntityManagerFactoryBean): JpaTransactionManager {
        return JpaTransactionManager().apply {
            this.entityManagerFactory = entityManagerFactoryBean.`object`
        }
    }

}

@Configuration
@ComponentScan("cf.vbnm.amoeba.repository.transition")
@EnableJpaRepositories(
    "cf.vbnm.amoeba.repository.transition",
    entityManagerFactoryRef = "transientEntityManagerFactory", transactionManagerRef = "transientTransactionManager"
)
open class TransientDatabaseConfiguration {

    @Bean("transientDataSource")
    open fun transientDataSource(
        @Value("\${amoeba.jdbc.transient.url:jdbc:h2:mem:amoeba}") url: String,
        @Value("\${amoeba.jdbc.transient.username:amoeba}") username: String,
        @Value("\${amoeba.jdbc.transient.password:amoeba}") password: String,
    ): DataSource {
        return JdbcDataSource().apply {
            this.user = username
            this.password = password
            this.setUrl(url)
        }
    }

    @Bean("transientEntityManagerFactory")
    open fun entityManagerFactory(
        @Qualifier("transientDataSource") dataSource: DataSource,
        @Qualifier("transientJpaVendorAdapter") jpaVendorAdapter: HibernateJpaVendorAdapter
    ): LocalContainerEntityManagerFactoryBean {
        return LocalContainerEntityManagerFactoryBean().apply {
            this.dataSource = dataSource
            this.jpaVendorAdapter = jpaVendorAdapter
            this.setPackagesToScan("cf.vbnm.amoeba.entity")
            persistenceProvider = HibernatePersistenceProvider()
            jpaDialect = HibernateJpaDialect()
        }
    }

    @Bean("transientJpaVendorAdapter")
    open fun jpaVendorAdapter(): HibernateJpaVendorAdapter {
        return HibernateJpaVendorAdapter().apply {
            this.setDatabase(Database.H2)
            this.setGenerateDdl(false)
            this.setShowSql(true)
            this.setDatabasePlatform(H2Dialect::class.java.name)
        }
    }


    @Bean("transientTransactionManager")
    open fun transactionManager(@Qualifier("transientEntityManagerFactory") entityManagerFactoryBean: LocalContainerEntityManagerFactoryBean): JpaTransactionManager {
        return JpaTransactionManager().apply {
            this.entityManagerFactory = entityManagerFactoryBean.`object`
        }
    }

}