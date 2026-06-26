package com.example.j2n.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommonUtilsTest {

    @Test
    void safeTrim_ShouldReturnNull_WhenInputIsNull() {
        assertNull(CommonUtils.safeTrim(null));
    }

    @Test
    void safeTrim_ShouldReturnEmptyString_WhenInputIsEmpty() {
        assertEquals("", CommonUtils.safeTrim(""));
        assertEquals("", CommonUtils.safeTrim("   "));
    }

    @Test
    void safeTrim_ShouldReturnTrimmedString_WhenInputHasWhitespaces() {
        assertEquals("hello", CommonUtils.safeTrim("hello"));
        assertEquals("hello", CommonUtils.safeTrim(" hello "));
        assertEquals("hello world", CommonUtils.safeTrim("  hello world  "));
    }
}
