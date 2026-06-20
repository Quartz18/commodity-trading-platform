package com.company.trade.integration;

import com.company.trade.dto.TradeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import com.company.trade.domain.TradeSide;

import java.math.BigDecimal;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Sql(scripts = "/test-data.sql")
class TradeControllerIntegrationTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
                .withDatabaseName("trading_test_db")
                .withUsername("test_user")
                .withPassword("test_password");
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private RestTestClient restTestClient;

    @Test
    void shouldBookTradeSuccessfully() {
        TradeRequest request = new TradeRequest();
        request.setCommodity("GOLD");
        request.setSide(TradeSide.BUY);
        request.setQuantity(new BigDecimal(10));
        request.setPrice(new BigDecimal("100"));

        restTestClient.post()
                .uri("/trades")
                .body(request)
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    void shouldBookTradeAndReturnPnl() {
        TradeRequest request = new TradeRequest();
        request.setCommodity("GOLD");
        request.setSide(TradeSide.BUY);
        request.setQuantity(new BigDecimal(10));
        request.setPrice(new BigDecimal("100"));

        restTestClient.post()
                .uri("/trades")
                .body(request)
                .exchange();
        restTestClient.get()
                .uri("/trades/pnl/GOLD")
                .exchange()
                .expectStatus().isOk();
    }
}