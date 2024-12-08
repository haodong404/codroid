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

package org.codroid.interfaces.appearance.editor;

import android.text.Spannable;
import android.text.Spanned;

import org.codroid.interfaces.addon.AddonManager;
import org.codroid.interfaces.appearance.Semantic;

import java.util.Arrays;

/**
 * This class is a wrapped spannable,
 * which is convenient to set spans on the special texts.
 */
public class WrappedSpannable {

    private Spannable spannable;

    public WrappedSpannable(Spannable spannable) {
        this.spannable = spannable;
    }

    public Spannable spannable() {
        return this.spannable;
    }

    /**
     * It's called when global spannable changed.
     *
     * @param spannable it's always from CodroidEditor.
     * @return this instance.
     */
    public WrappedSpannable update(Spannable spannable) {
        this.spannable = spannable;
        return this;
    }

    /**
     * Attach a semantic span to the text.
     *
     * @param start where the position started.
     * @param end   where the position ended.
     * @param what  the semantic span
     */
    public void endowSemantic(int start, int end, Class<? extends Semantic> what) {
        if (what != null) {
            if (!isSemanticExists(start, end)) {
                try {
                    spannable.setSpan(what.newInstance(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    AddonManager.get().getLogger().e("Highlight Error: " + e);
                }
            }
        }
    }

    /**
     * Determine whether a span is semantics.
     *
     * @param start where the position started.
     * @param end where the position ended.
     * @return true if exists.
     */
    private boolean isSemanticExists(int start, int end) {
        Semantic[] spans = spannable.getSpans(start, end, Semantic.class);
        if (spans.length == 1) return spannable.getSpanEnd(spans[0]) >= end;

        if (spans.length > 1) {
            Arrays.stream(spans).forEach(i -> {
                spannable.removeSpan(i);
            });
        }
        return false;
    }
}
