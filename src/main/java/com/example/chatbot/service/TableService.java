package com.example.chatbot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TableService {

    private static final int MAX_ABS_NUMBER = 1_000_000;
    private static final int ROW_LIMIT = 10;

    public void validateNumber(Integer number) {
        if (number == null) {
            throw new IllegalArgumentException("number is required");
        }
        if (Math.abs((long) number) > MAX_ABS_NUMBER) {
            throw new IllegalArgumentException("number out of allowed range (abs(number) <= 1000000)");
        }
    }

    // Programmatic table generation returning DTOs was removed in favor of
    // returning a single formatted string. If a programmatic API is needed
    // later, reintroduce a TableRow DTO with appropriate constructors.

    /**
     * Generate a human-readable multiplication table as a single String with newlines.
     * Example for number=2:
     * 1 X 2 = 2\n2 X 2 = 4\n...
     */
    public String generateTableString(int number) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= ROW_LIMIT; i++) {
            long value = (long) number * i;
            sb.append(i).append(" X ").append(number).append(" = ").append(value);
            sb.append(", ");
        }
        return sb.toString();
    }

}
