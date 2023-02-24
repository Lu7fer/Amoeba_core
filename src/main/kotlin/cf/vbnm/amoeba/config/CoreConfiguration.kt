package cf.vbnm.amoeba.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing


@Configuration
@EnableJpaAuditing
@Import(PersistenceDatabaseConfiguration::class)
@ComponentScan("cf.vbnm.amoeba.core")
open class CoreConfiguration
