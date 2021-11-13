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
import android.util.Log;

import org.codroid.interfaces.addon.AddonManager;
import org.codroid.interfaces.appearance.Semantic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WrappedSpannable {

    public final static String TYPE_KEYWORD = "keyword";

    private final Map<String, Class<?>> semantics = new HashMap<>() {{
        put(TYPE_KEYWORD, Keyword.class);
    }};

    private Spannable spannable;

    public WrappedSpannable(Spannable spannable) {
        this.spannable = spannable;
    }

    public Spannable spannable() {
        return this.spannable;
    }

    public WrappedSpannable update(Spannable spannable) {
        this.spannable = spannable;
        return this;
    }

    public void endowSemantic(String what, int start, int end) {
        Class<?> span = semantics.get(what);
        if (span != null) {
            if (!isSemanticExists(start, end)) {
                Log.d("Zac", "Semantic");
                try {
                    spannable.setSpan(span.newInstance(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    AddonManager.get().getLogger().e("Highlight Error: " + e);
                }
            }
        }
    }

    private boolean isSemanticExists(int start, int end) {
        Semantic[] spans = spannable.getSpans(start, end, Semantic.class);
        if (spans.length == 1)
            return spannable.getSpanEnd(spans[0]) >= end;

        if (spans.length > 1) {
            Arrays.stream(spans).forEach(i -> {
                spannable.removeSpan(i);
            });
        }
        return false;
    }
}
