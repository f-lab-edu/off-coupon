package com.flab.offcoupon;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest
@Disabled("쿼리 속도 측정을 위한 테스트 코드")
public class QueryTest {

    @Value("${spring.datasource.url.query-test}")
    private String url;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;
    private static final int queryCount = 10;
    private static long totalExecutionTimeWithIndex = 0;
    private static long totalExecutionTimeWithoutIndex = 0;
    private static Connection connection;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        String url = System.getProperty("spring.datasource.url.query-test");
        System.out.println("url : " + url);
        String user = System.getProperty("spring.datasource.username");
        String password = System.getProperty("spring.datasource.password");
        connection = DriverManager.getConnection(url, user, password);
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    @DisplayName("[쿼리 실행 시간 측정] 인덱스 있는 쿼리 실행 시간 비교")
    void testQueryWithIndex() throws SQLException {
        for (int i = 0; i < queryCount; i++) {
            long startTime = System.currentTimeMillis(); // 시작 시간 기록
            try (Statement statement = connection.createStatement()) {
                String sql = "SELECT e.category, e.description, c.discount_type, c.discount_rate, c.discount_price, c.validate_start_date, c.validate_end_date, ci.coupon_status FROM coupon_issue ci JOIN coupon c on ci.coupon_id = c.id JOIN event e on c.event_id = e.id WHERE ci.member_id = 1;";
                ResultSet resultSet = statement.executeQuery(sql);
                // 쿼리 실행 결과 사용하지 않음 (예제에서는 단순 실행 시간 측정 목적)
            }
            long endTime = System.currentTimeMillis(); // 종료 시간 기록
            long executionTime = endTime - startTime; // 실행 시간 계산
            totalExecutionTimeWithIndex += executionTime; // 총 실행 시간 누적
            System.out.println("Query with index " + (i + 1) + ": Execution time " + executionTime + " milliseconds");
        }
    }

    @Test
    @DisplayName("[쿼리 실행 시간 측정] 인덱스 없는 쿼리 실행 시간 비교")
    void testQueryWithoutIndex() throws SQLException {
        for (int i = 0; i < queryCount; i++) {
            long startTime = System.currentTimeMillis(); // 시작 시간 기록
            try (Statement statement = connection.createStatement()) {
                String sql = "SELECT e.category, e.description, c.discount_type, c.discount_rate, c.discount_price, c.validate_start_date, c.validate_end_date, ci.coupon_status FROM coupon_issue_no_idx ci JOIN coupon c on ci.coupon_id = c.id JOIN event e on c.event_id = e.id WHERE ci.member_id = 1;";
                ResultSet resultSet = statement.executeQuery(sql);
                // 쿼리 실행 결과 사용하지 않음 (예제에서는 단순 실행 시간 측정 목적)
            }
            long endTime = System.currentTimeMillis(); // 종료 시간 기록
            long executionTime = endTime - startTime; // 실행 시간 계산
            totalExecutionTimeWithoutIndex += executionTime; // 총 실행 시간 누적
            System.out.println("Query without index " + (i + 1) + ": Execution time " + executionTime + " milliseconds");
        }
    }

    @Test
    @DisplayName("[평균 실행 시간 계산] 인덱스 생성 전후 쿼리 실행 시간 비교")
    void testAverageExecutionTime() {
        // 평균 실행 시간 계산
        double averageExecutionTimeWithIndex = (double) totalExecutionTimeWithIndex / queryCount;
        double averageExecutionTimeWithoutIndex = (double) totalExecutionTimeWithoutIndex / queryCount;
        System.out.println("Average execution time with index: " + averageExecutionTimeWithIndex + " milliseconds");
        System.out.println("Average execution time without index: " + averageExecutionTimeWithoutIndex + " milliseconds");
        assertTrue(averageExecutionTimeWithIndex < averageExecutionTimeWithoutIndex);
    }
}