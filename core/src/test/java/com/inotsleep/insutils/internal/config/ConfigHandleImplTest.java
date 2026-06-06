package com.inotsleep.insutils.internal.config;

import com.inotsleep.insutils.api.config.Path;
import com.inotsleep.insutils.api.config.TypeKey;
import com.inotsleep.insutils.api.service.ServiceManager;
import com.inotsleep.insutils.internal.logging.LoggingManagerImpl;
import com.inotsleep.insutils.spi.config.Config;
import com.inotsleep.insutils.spi.config.SerializableObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigHandleImplTest {
    private ConfigHandleImpl handle;

    @BeforeEach
    void setUp() {
        if (ServiceManager.get(com.inotsleep.insutils.api.logging.LoggingManager.class) == null) {
            ServiceManager.register(com.inotsleep.insutils.api.logging.LoggingManager.class, new LoggingManagerImpl());
        }
        handle = new ConfigHandleImpl();
    }

    @Test
    void serializesAndDeserializesNestedGenericStructures() {
        Map<String, List<TestSection>> value = new LinkedHashMap<>();
        value.put("first", List.of(section("alpha", 1), section("beta", 2)));
        value.put("second", List.of(section("gamma", 3)));

        TypeKey<Map<String, List<TestSection>>> type = new TypeKey<>() {};

        var node = handle.serialize(type, value);
        Map<String, List<TestSection>> restored = handle.deserialize(type, node);

        assertNotNull(restored);
        assertEquals(value.keySet(), restored.keySet());
        assertSections(value.get("first"), restored.get("first"));
        assertSections(value.get("second"), restored.get("second"));
    }

    @Test
    void savesAndReloadsConfigRoundTrip(@TempDir java.nio.file.Path tempDir) throws Exception {
        File configFile = tempDir.resolve("config.yml").toFile();
        TestConfig written = new TestConfig(configFile);
        written.name = "demo";
        written.mode = TestMode.ADVANCED;
        written.flags = new LinkedHashSet<>(List.of("one", "two"));
        written.levels = new ArrayList<>(List.of(3, 5, 8));
        written.sections = new LinkedHashMap<>();
        written.sections.put("primary", section("alpha", 11));
        written.sections.put("secondary", section("beta", 22));
        written.groupedSections = new LinkedHashMap<>();
        written.groupedSections.put("pack-a", List.of(section("nested-a", 7), section("nested-b", 9)));

        handle.saveConfig(written);

        String yaml = Files.readString(configFile.toPath());
        assertTrue(yaml.contains("name"));
        assertTrue(yaml.contains("grouped-sections"));
        assertTrue(yaml.contains("ADVANCED"));

        TestConfig reloaded = new TestConfig(configFile);
        handle.reloadConfig(reloaded);

        assertEquals(written.name, reloaded.name);
        assertEquals(written.mode, reloaded.mode);
        assertEquals(written.flags, reloaded.flags);
        assertEquals(written.levels, reloaded.levels);
        assertSectionsMap(written.sections, reloaded.sections);
        assertGroupedSections(written.groupedSections, reloaded.groupedSections);
    }

    private static TestSection section(String label, int amount) {
        TestSection section = new TestSection();
        section.label = label;
        section.amount = amount;
        return section;
    }

    private static void assertSections(List<TestSection> expected, List<TestSection> actual) {
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        for (int index = 0; index < expected.size(); index++) {
            assertSection(expected.get(index), actual.get(index));
        }
    }

    private static void assertSectionsMap(Map<String, TestSection> expected, Map<String, TestSection> actual) {
        assertNotNull(actual);
        assertEquals(expected.keySet(), actual.keySet());
        for (Map.Entry<String, TestSection> entry : expected.entrySet()) {
            assertSection(entry.getValue(), actual.get(entry.getKey()));
        }
    }

    private static void assertGroupedSections(Map<String, List<TestSection>> expected, Map<String, List<TestSection>> actual) {
        assertNotNull(actual);
        assertEquals(expected.keySet(), actual.keySet());
        for (Map.Entry<String, List<TestSection>> entry : expected.entrySet()) {
            assertSections(entry.getValue(), actual.get(entry.getKey()));
        }
    }

    private static void assertSection(TestSection expected, TestSection actual) {
        assertNotNull(actual);
        assertEquals(expected.label, actual.label);
        assertEquals(expected.amount, actual.amount);
    }

    enum TestMode {
        BASIC,
        ADVANCED
    }

    public static class TestSection extends SerializableObject {
        @Path("label")
        public String label;

        @Path("amount")
        public int amount;
    }

    public static class TestConfig extends Config {
        @Path("name")
        public String name = "unset";

        @Path("mode")
        public TestMode mode = TestMode.BASIC;

        @Path("flags")
        public Set<String> flags = new LinkedHashSet<>();

        @Path("levels")
        public List<Integer> levels = new ArrayList<>();

        @Path("sections")
        public Map<String, TestSection> sections = new LinkedHashMap<>();

        @Path("grouped-sections")
        public Map<String, List<TestSection>> groupedSections = new LinkedHashMap<>();

        public TestConfig(File configFile) {
            super(configFile);
        }
    }
}
