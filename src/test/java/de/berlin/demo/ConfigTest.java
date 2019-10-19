package de.berlin.demo;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigTest {

    private static Config conf = null;
    private static Config testConf = null;

    @BeforeEach
    void setUp() {
        conf = ConfigFactory.load();
        testConf = ConfigFactory.load("test");
    }

    @Test
    void testSimpleConfig() {
        assertEquals("42", conf.getString("simple-app.answer"));
    }

    @Test
    void testConfig() {
        assertNotNull(testConf);
        assertEquals("test", testConf.getString("app.answer"));
    }

    @Test
    void analyticaTestConfig() {
        String testStr = conf.getString("analytica.TEST");
        assertEquals("test", testStr);
    }

    @AfterEach
    void tearDown() {

    }
}
