package com.demo.config;

import com.zaxxer.hikari.HikariDataSource;
import io.github.jleblanc64.hibernate6.hibernate.VavrHibernate6;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Value("${spring.datasource.url}")
    String url;

    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    String password;

    @Bean
    public DataSource getDataSource() {
        VavrHibernate6.override();

        // Hikari
        var ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);

        // Flyway migration
        var config = Flyway.configure().dataSource(url, username, password);
        config.load().migrate();

        return ds;
    }
}
