package com.example.demo.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.yml")
public class ValidateHeaderTest {

    @Autowired
    ValidateHeader validateHeader;

    @Test
    void test_headerValidation(){
        Assertions.assertEquals(true, validateHeader.validateSecret("12345"));
    }
}
