package ru.melowetty.servicediscovery.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val env: Environment
) {
    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .csrf {
                it.disable()
            }
            .httpBasic {

            }
            .authorizeHttpRequests {
                it.requestMatchers("/eureka/**").hasAnyRole("SYSTEM", "ADMIN")
                it.anyRequest().hasRole("ADMIN")
            }.build()
    }

    @Bean
    fun userDetails(manager: AuthenticationManagerBuilder,
    ): UserDetailsService {
        val adminUsername = env["service-discovery.admin.user"]!!
        val adminPassword = env["service-discovery.admin.password"]!!

        val admin = User
            .withUsername(adminUsername)
            .password("{noop}${adminPassword}")
            .roles("ADMIN")
            .build()

        val systemUsername = env["service-discovery.user"]!!
        val systemPassword = env["service-discovery.password"]!!

        val system = User
            .withUsername(systemUsername)
            .password("{noop}${systemPassword}")
            .roles("SYSTEM")
            .build()

        return InMemoryUserDetailsManager(admin, system)

    }
}