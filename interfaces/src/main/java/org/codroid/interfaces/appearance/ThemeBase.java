package org.codroid.interfaces.appearance;

import org.codroid.interfaces.Property;
import org.codroid.interfaces.CodroidEnv;
import org.codroid.interfaces.addon.Addon;

public abstract class ThemeBase extends CodroidEnv implements Addon {

    @Override
    public void onLoading() {

    }

    protected abstract Property loadTheme();

    @Override
    public void onAppExited() {

    }

}
