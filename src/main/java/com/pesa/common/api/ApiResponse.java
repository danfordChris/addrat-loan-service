package com.pesa.common.api;

import java.time.Instant;
import java.util.Map;

public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    Map<String, String> errors,
    Instant timestamp
) {}
