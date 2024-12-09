package org.codroid.interfaces.addon;

import org.codroid.interfaces.log.Loggable;

public interface Addon extends Loggable {

    void onLoading();

    void onAppExited();
}
