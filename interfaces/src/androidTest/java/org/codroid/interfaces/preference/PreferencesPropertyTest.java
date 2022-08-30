package org.codroid.interfaces.preference;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;

import com.tencent.mmkv.MMKV;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        PreferenceProperty property = new PreferenceProperty("ID", "", input);
        Assert.assertFalse(property.getBoolean("switch"));
    }
}