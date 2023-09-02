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

package org.codroid.body.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class BottomPanel extends FrameLayout {

    private float downPointX, downPointY;

    private BottomSheetBehavior<BottomPanel> behavior;

    public BottomPanel(@NonNull Context context) {
        super(context);
    }

    public BottomPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BottomPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        setOnClickListener(v -> {});
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.behavior = BottomSheetBehavior.from(this);
        if (getChildCount() == 0) {
            return;
        }

        // Layout the peek
        getChildAt(0).layout(left, top, right, bottom);
        getChildAt(1).layout(left, behavior.getPeekHeight(), right, bottom);
    }

    public void show() {
        if (isCollapsed()) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void close() {
        if (!isCollapsed()) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void moveBy(float y) {
        setY(getY() + y);
    }

    public boolean isCollapsed() {
        return behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED;
    }

}
