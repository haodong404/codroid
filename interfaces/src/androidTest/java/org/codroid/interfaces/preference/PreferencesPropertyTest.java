package org.codroid.interfaces.preference;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.tencent.mmkv.MMKV;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Path;

@SmallTest
public class PreferencesPropertyTest {

    private Path tomlPath;

    @Before
    public void init() {
        try {
            MMKV.initialize(ApplicationProvider.getApplicationContext());
            var a = getClass().getResourceAsStream("preferences.toml");
            System.out.println(a.available());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestInitialValue() {
        PreferencesProperty property = new PreferencesProperty(tomlPath);
        Assert.assertFalse(property.getBoolean("switch"));
    }
}