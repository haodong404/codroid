package org.codroid.interfaces.evnet.editor;

import org.codroid.interfaces.appearance.editor.WrappedSpannable;
import org.codroid.interfaces.evnet.Event;

public interface TextChangedEvent extends Event {

    /**
     * Called when text in editor changed.
     *
     * @param wrappedSpannable spannable
     * @param start the starting position
     * @param lengthBefore length before
     * @param lengthAfter length after
     */
    void onTextChanged(WrappedSpannable wrappedSpannable, int start, int lengthBefore, int lengthAfter);

}
