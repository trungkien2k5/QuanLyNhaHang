package com.kien.payment.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class MonAnClient {

    private final RestClient restClient;

    public MonAnClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder builder
    ) {
        this.restClient = builder
                .baseUrl("http://restaurant-service")
                .build();
    }

    public BigDecimal layDonGia(Integer maMon) {
        MonAnResponse response = restClient.get()
                .uri("/monan/{id}", maMon)
                .retrieve()
                .body(MonAnResponse.class);

        if (response == null || response.data() == null) {
            throw new RuntimeException("Không tìm thấy món ăn");
        }

        return response.data().donGia();
    }

    public record MonAnResponse(MonAnData data) {}

    public record MonAnData(
            Integer maMon,
            BigDecimal donGia
    ) {}
}