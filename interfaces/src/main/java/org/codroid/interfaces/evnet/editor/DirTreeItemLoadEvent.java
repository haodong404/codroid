package org.codroid.interfaces.evnet.editor;

import org.codroid.interfaces.evnet.Event;
import org.codroid.interfaces.evnet.entities.DirTreeItemEntity;

import java.io.File;

public interface DirTreeItemLoadEvent extends Event {

    /**
     * Called before item loaded.
     * It called in a non-main thread.
     * @param file file
     * @return what changes you made.
     */
    DirTreeItemEntity beforeLoading(File file);

}
