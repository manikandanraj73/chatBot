package com.example.chatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TableServiceTest {

    private final TableService service = new TableService();

    @Test
    public void testValidateNumber_null_throws() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.validateNumber(null));
        assertEquals("number is required", ex.getMessage());
    }

    @Test
    public void testValidateNumber_outOfRange_throws() {
        int big = 1_000_001;
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.validateNumber(big));
        assertEquals("number out of allowed range (abs(number) <= 1000000)", ex.getMessage());
    }

    @Test
    public void testGenerateTableString_correctness() {
        String s = service.generateTableString(5);
        String[] lines = s.split("\\\\n");
        assertEquals(10, lines.length);
        assertEquals("1 X 5 = 5", lines[0]);
        assertEquals("10 X 5 = 50", lines[9]);
    }

    @Test
    public void testGenerateTableString_negativeNumber() {
        String s = service.generateTableString(-3);
        String[] lines = s.split("\\\\n");
        assertEquals(10, lines.length);
        assertEquals("1 X -3 = -3", lines[0]);
        assertEquals("10 X -3 = -30", lines[9]);
    }
}
