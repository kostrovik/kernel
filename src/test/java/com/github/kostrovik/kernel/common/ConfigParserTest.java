package com.github.kostrovik.kernel.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-12-16
 * github:  https://github.com/kostrovik/kernel
 */
public class ConfigParserTest {
    private ConfigParser parser;
    private Path configFile;

    @BeforeEach
    void init() {
        configFile = Paths.get(this.getClass().getResource("/com/github/kostrovik/kernel/common/test_config.yaml").getPath());
        parser = new ConfigParser(configFile);
    }

    @Test
    void findPropertyTest() {
        Object value = parser.getConfigProperty("attribute_2");
        assertEquals("attr_value_2", value);
    }

    @Test
    void findPropertyTest1() {
        Object value = parser.getConfigProperty("attribute_3");
        assertNull(value);
    }

    @Test
    void findPropertyTest2() {
        Object value = parser.getConfigProperty("itemTypeList");
        assertTrue(value instanceof Map);
        assertEquals("Типы номенклатуры", ((Map) value).get("title"));
    }

    @Test
    void findPropertyTest3() {
        Object value = parser.getConfigProperty("title");
        assertEquals("Справочники", value);
    }
}