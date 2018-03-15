package org.jetbrains.dekaf.core;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@SuppressWarnings("SpellCheckingInspection")
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


    @Test
    void override_append() {
        Settings settings = new Settings("Param1", "Value1",
                                         "Param2", "Value2");
        Settings newSettings = settings.override("Extra", "YYY");

        assertThat(newSettings.toMap())
            .containsKeys("Param1", "Param2", "Extra")
            .containsEntry("Extra", "YYY");
    }

    @Test
    void override_change() {
        Settings settings = new Settings("Param1", "Value1",
                                         "Param2", "Value2",
                                         "Param3", "Value3");
        Settings newSettings = settings.override("Param2", "NewValue");

        assertThat(newSettings.toMap())
            .containsKeys("Param1", "Param2", "Param3")
            .containsEntry("Param2", "NewValue");
    }

    @Test
    void override_remove() {
        Settings settings = new Settings("Param1", "Value1",
                                         "Param2", "Value2",
                                         "Param3", "Value3");
        Settings newSettings = settings.override("Param2", null);

        assertThat(newSettings.toMap())
            .containsKeys("Param1", "Param3")
            .doesNotContainKey("Param2");
    }

    @Test
    void override_asIs() {
        Settings settings = new Settings("Param1", "Value1",
                                         "Param2", "Value2",
                                         "Param3", "Value3");
        Settings newSettings = settings.override("Param2", "Value2",
                                                 "Labuda", null);
        assertThat(newSettings).isSameAs(settings);
    }


    @Test
    void toProperties() {
        Settings settings =
            new Settings(
                "a.b.c", "ABC",
                "d.e.f", "DEF",
                "x.y.z", "XYZ"
            );
        Properties properties = settings.toProperties(null);

        assertThat(properties)
            .containsKeys("a.b.c", "d.e.f", "x.y.z")
            .containsEntry("a.b.c", "ABC")
            .containsEntry("d.e.f", "DEF")
            .containsEntry("x.y.z", "XYZ")
            .hasSize(3);
    }

    
    @Test
    void toSubgroupProperties() {
        Settings settings =
            new Settings(
                "mura.1", "1",
                "mura.2", "2",
                "matter.A", "A",
                "matter.B", "B",
                "matter.B.X", "BX",
                "labuda.oo", "oo",
                "labuda.ooo", "ooo"
            );
        Properties properties = settings.toSubgroupProperties(null, "matter");

        assertThat(properties)
            .containsKeys("A", "B", "B.X")
            .containsEntry("A", "A")
            .containsEntry("B", "B")
            .containsEntry("B.X", "BX")
            .hasSize(3);
    }


}
