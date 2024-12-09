package org.codroid.interfaces.evnet.editor;

import org.codroid.interfaces.appearance.editor.WrappedSpannable;
import org.codroid.interfaces.evnet.Event;

public interface SelectionChangedEvent extends Event {

    /**
     * Called when some text is selected.
     * @param spannable spannable
     * @param selStart the starting position
     * @param selEnd the ending position
     */
    void onSelectionChanged(WrappedSpannable spannable, int selStart, int selEnd);

}
