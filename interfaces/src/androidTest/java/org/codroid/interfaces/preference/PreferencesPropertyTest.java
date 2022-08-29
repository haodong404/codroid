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
import java.io.IOException;
import java.nio.file.Path;

@SmallTest
public class PreferencesPropertyTest {

    private Path tomlPath;

    @Before
    public void init() {
        MMKV.initialize(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void TestInitialValue() throws IOException {
        var input = ApplicationProvider.getApplicationContext().getAssets().open("preferences.toml");
        PreferencesProperty property = new PreferencesProperty("ID", "", input);
        Assert.assertFalse(property.getBoolean("switch"));
    }
}