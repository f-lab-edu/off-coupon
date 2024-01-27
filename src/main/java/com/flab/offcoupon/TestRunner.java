package com.flab.offcoupon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestRunner implements ApplicationRunner {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Connection connection = dataSource.getConnection();
        log.info("DBCP:{}", dataSource.getClass());
        log.info("Url:{}", connection.getMetaData().getURL());
        log.info("username:{}", connection.getMetaData().getUserName());

        jdbcTemplate.execute("INSERT INTO Member(name, password, email) VALUES ('Lion', '1234', 'lion@naver.com')");
    }
}
