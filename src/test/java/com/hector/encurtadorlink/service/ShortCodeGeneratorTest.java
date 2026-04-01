package com.hector.encurtadorlink.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ShortCodeGenerator Tests")
class ShortCodeGeneratorTest {

    private ShortCodeGenerator shortCodeGenerator;

    @BeforeEach
    void setUp() {
        shortCodeGenerator = new ShortCodeGenerator();
    }

    @Test
    @DisplayName("Should generate short code with length 6")
    void testGenerateLengthShouldBeSix() {
        String code = shortCodeGenerator.generate();
        assertEquals(6, code.length());
    }

    @Test
    @DisplayName("Should generate short code with only base62 characters")
    void testGenerateOnlyBase62Characters() {
        String base62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        for (int i = 0; i < 100; i++) {
            String code = shortCodeGenerator.generate();
            for (char c : code.toCharArray()) {
                assertTrue(base62.indexOf(c) >= 0, "Character " + c + " is not in base62");
            }
        }
    }

    @Test
    @DisplayName("Should generate different codes on multiple calls")
    void testGenerateDifferentCodes() {
        String code1 = shortCodeGenerator.generate();
        String code2 = shortCodeGenerator.generate();
        String code3 = shortCodeGenerator.generate();
        
        assertNotEquals(code1, code2);
        assertNotEquals(code2, code3);
        assertNotEquals(code1, code3);
    }

    @Test
    @DisplayName("Should generate unique codes (with low probability of collision)")
    void testGenerateUniqueCodes() {
        Set<String> codes = new HashSet<>();
        int iterations = 1000;
        
        for (int i = 0; i < iterations; i++) {
            codes.add(shortCodeGenerator.generate());
        }
        
        
        assertTrue(codes.size() >= 900, "Too many collisions detected");
    }

    @Test
    @DisplayName("Should not generate null or empty codes")
    void testGenerateNotNullOrEmpty() {
        for (int i = 0; i < 100; i++) {
            String code = shortCodeGenerator.generate();
            assertNotNull(code);
            assertFalse(code.isEmpty());
        }
    }
}
