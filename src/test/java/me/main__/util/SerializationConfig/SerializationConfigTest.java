package me.main__.util.SerializationConfig;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import me.main__.util.SerializationConfig.util.*;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SerializationConfigTest {

    TestConfiguration testConfig;

    String lastNotification = null;

    @Before
    public void setUp() throws Exception {
        testConfig = new TestConfiguration();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws Exception {
        try {
            // register classes
            SerializationConfig.registerAll(TestConfiguration.class);

            File configFile = new File("testConfig.yml");
            configFile.createNewFile();
            FileConfiguration config1 = YamlConfiguration.loadConfiguration(configFile); // load empty configuration

            // store the original testConfig
            config1.set("testobject", testConfig);
            // and save
            config1.save(configFile);

            // load it once again
            FileConfiguration config2 = YamlConfiguration.loadConfiguration(configFile);
            testConfig = (TestConfiguration) config2.get("testobject");
            assertEquals("test1", testConfig.test1);
            assertEquals("test2", testConfig.test2);
            assertEquals("awesome", testConfig.custom.val);
            assertEquals(false, testConfig.bool);
            assertEquals(Arrays.asList("defaultEntry"), testConfig.stringList);
            assertEquals("subTest", testConfig.subConfig.val);
            testConfig.test1 = "new";
            testConfig.test2 = "new";
            assertFalse(testConfig.setPropertyValue("bOOl", true, false));
            assertTrue(testConfig.setPropertyValue("bOOl", true, true));
            assertEquals(true, testConfig.bool);
            testConfig.stringList.add("new");
            testConfig.custom.val = "new";
            assertFalse(testConfig.setProperty("invalidproperty", "invalidvalue"));
            assertTrue(testConfig.setProperty("subConfig.val", "new"));
            assertEquals("new", testConfig.subConfig.val);
            config2.set("testobject", testConfig);
            config2.save(configFile);

            // 3rd round
            FileConfiguration config3 = YamlConfiguration.loadConfiguration(configFile);
            testConfig = (TestConfiguration) config3.get("testobject");
            assertEquals("new", testConfig.test1);
            assertEquals("test2", testConfig.test2);
            assertEquals(true, testConfig.bool);
            assertEquals(Arrays.asList("defaultEntry", "new"), testConfig.stringList);
            assertEquals("new", testConfig.custom.val);
            assertEquals("new", testConfig.subConfig.val);

            // validator-tests 1
            assertEquals("validatorTest1", testConfig.validatorTest1);
            assertTrue(testConfig.setProperty("validatorTest1", "awesomeValue"));
            assertEquals("awesomeValue", testConfig.validatorTest1);
            assertFalse(testConfig.setProperty("validatorTest1", "denyThis!"));
            assertEquals("awesomeValue", testConfig.validatorTest1);
            assertTrue(testConfig.setProperty("validatorTest1", "silentFail"));
            assertEquals("awesomeValue", testConfig.validatorTest1);
            assertTrue(testConfig.setProperty("validatorTest1", "strange...?"));
            assertEquals("strangeValue", testConfig.validatorTest1);

            // validator-tests 2
            assertEquals("validatorTest2", testConfig.validatorTest2);
            assertTrue(testConfig.setProperty("validatorTest2", "newValue"));
            assertEquals("newValue", testConfig.validatorTest2);

            // yet MORE validator-tests (yes, we're testing EVERYTHING here and I'm REALLY proud of that fact.)
            TestValidator.notification = new TestValidator.Notification() {
                @Override
                public void call(String name) {
                    lastNotification = name;
                }
            };
            ValidateAllTestConfig vatc = new ValidateAllTestConfig();
            assertNull(lastNotification);
            assertEquals("propWithInheritedValidator", vatc.propWithInheritedValidator);
            assertTrue(vatc.setProperty("propWithInheritedValidator", "newVal"));
            assertEquals("propWithInheritedValidator", lastNotification);
            lastNotification = null;
            assertEquals("newVal", vatc.propWithInheritedValidator);
            assertTrue(vatc.setProperty("propWithOverriddenValidator", "newVal"));
            assertNull(lastNotification);
            assertEquals("newVal", vatc.propWithOverriddenValidator);
        } finally {
            new File("testConfig.yml").delete();
        }
    }

}
