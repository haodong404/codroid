/*
 *     Copyright (c) 2021 Zachary. All rights reserved.
 *
 *     This file is part of Codroid.
 *
 *     Codroid is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Codroid is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Codroid.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.codroid.editor.widget;

import android.graphics.Color;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import org.codroid.interfaces.appearance.editor.Keyword;
import org.codroid.interfaces.appearance.editor.Operator;
import org.codroid.interfaces.appearance.editor.WrappedSpannable;
import org.codroid.interfaces.env.AddonEnv;
import org.codroid.interfaces.evnet.editor.SelectionChangedEvent;
import org.codroid.interfaces.evnet.editor.TextChangedEvent;

public class TestEvent implements SelectionChangedEvent, TextChangedEvent {

    private ForegroundColorSpan colorSpan;

    public TestEvent() {
        colorSpan = new ForegroundColorSpan(Color.RED);
    }

    @Override
    public void attached(AddonEnv addon) {

    }

    @Override
    public void onSelectionChanged(WrappedSpannable wrp, int selStart, int selEnd) {
        Spannable spannable = wrp.spannable();
        if (selStart == selEnd) {
            if (selStart > 0) {
                if (spannable.charAt(selStart) == '+' && spannable.getSpanStart(colorSpan) == -1) {
                    spannable.setSpan(colorSpan, selStart, selStart + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                wrp.endowSemantic(selStart - 2, selEnd, Keyword.class);
                return;
            }
        }
        spannable.removeSpan(colorSpan);
    }

    @Override
    public void onTextChanged(WrappedSpannable wrappedSpannable, int start, int lengthBefore, int lengthAfter) {
        Spannable spannable = wrappedSpannable.spannable();
        wrappedSpannable.endowSemantic(start, start + lengthAfter, Operator.class);
    }
}
