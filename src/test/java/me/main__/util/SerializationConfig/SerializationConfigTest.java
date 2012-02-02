package me.main__.util.SerializationConfig;

import static org.junit.Assert.*;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SerializationConfigTest {

    TestConfiguration testConfig;

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
            TestConfiguration.register();
            FileConfiguration config2 = YamlConfiguration.loadConfiguration(configFile);
            testConfig = (TestConfiguration) config2.get("testobject");
            assertEquals("test1", testConfig.test1);
            assertEquals("test2", testConfig.test2);
            assertEquals("awesome", testConfig.custom.val);
            assertEquals(false, testConfig.bool);
            assertEquals("subTest", testConfig.subConfig.val);
            testConfig.test1 = "new";
            testConfig.test2 = "new";
            testConfig.bool = true;
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
            assertEquals("new", testConfig.custom.val);
            assertEquals("new", testConfig.subConfig.val);
        }
        finally {
            new File("testConfig.yml").delete();
        }
    }

}
