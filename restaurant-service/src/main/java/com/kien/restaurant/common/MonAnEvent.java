package com.kien.restaurant.common;

import java.time.LocalDateTime;

public record MonAnEvent(
        String action,
        Integer maMon,
        String tenMon,
        LocalDateTime occurredAt) {
}
