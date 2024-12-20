package com.demo;

import com.demo.custom.HibernateList;
import com.demo.custom.HibernateOption;
import com.demo.custom.Jackson;
import com.zaxxer.hikari.HikariDataSource;
import io.github.jleblanc64.libcustom.LibCustom;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static io.github.jleblanc64.libcustom.functional.Functor.print;

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
        HibernateList.override();
        Jackson.override();

        HibernateOption.override();
        LibCustom.load();

        var ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);

        // default is 10
        ds.setMaximumPoolSize(8);

        var config = Flyway.configure().dataSource(url, username, password);
        config.load().migrate();

        return ds;
    }
}
