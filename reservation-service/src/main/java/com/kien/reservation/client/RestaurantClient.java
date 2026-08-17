package com.kien.reservation.client;

import com.kien.reservation.common.ApiResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class RestaurantClient {

    private final RestClient restClient;

    public RestaurantClient() {
    this.restClient = RestClient.builder()
            .baseUrl("http://localhost:8082")
            .build();
}

    public List<Integer> layMaBanTheoKhuVuc(Integer maKhuVuc) {
        ApiResponse<List<Integer>> response = restClient.get()
                .uri("/ban/khuvuc/{id}", maKhuVuc)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<List<Integer>>>() {});

        return response.getData();
    }
}