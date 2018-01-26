package org.jetbrains.dekaf.core;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UnitTest")
public class SettingsTest {

    @Test
    void basic() {
        Map<String,String> map = new HashMap<>();
        map.put("param1", "value1");
        map.put("param2", "value2");
        map.put("param3", "value3");

        Settings settings = new Settings(map);

        assertThat(settings.get("param1")).isEqualTo("value1");
        assertThat(settings.get("param2")).isEqualTo("value2");
        assertThat(settings.get("param3")).isEqualTo("value3");

        assertThat(settings.get("non-existent-param")).isNull();
        assertThat(settings.get("non-existent-param", "def")).isEqualTo("def");

        assertThat(settings.getSize()).isEqualTo(3);
    }

    @Test
    void basic_caseInsensitive() {
        Map<String,String> map = new HashMap<>();
        map.put("Param1", "Value1");
        map.put("Param2", "Value2");
        map.put("Param3", "Value3");

        Settings settings = new Settings(map);

        assertThat(settings.get("param2")).isEqualTo("Value2");
        assertThat(settings.get("PARAM2")).isEqualTo("Value2");
    }

    @Test
    void constructor_1() {
        Settings settings = new Settings("Param1", "Value1");

        assertThat(settings.get("Param1")).isEqualTo("Value1");

        assertThat(settings.getSize()).isEqualTo(1);
    }

    @Test
    void constructor_3() {
        Settings settings = new Settings("Param1", "Value1",
                                         "Param2", "Value2",
                                         "Param3", "Value3");

        assertThat(settings.get("Param1")).isEqualTo("Value1");
        assertThat(settings.get("Param2")).isEqualTo("Value2");
        assertThat(settings.get("Param3")).isEqualTo("Value3");

        assertThat(settings.getSize()).isEqualTo(3);
    }

    @Test
    void getNames_basic() {
        Settings settings = new Settings("Param1", "Value1",
                                         "Param2", "Value2",
                                         "Param3", "Value3");
        Set<String> names = settings.getNames();
        assertThat(names).contains("Param1", "Param2", "Param3");
    }
    
}
