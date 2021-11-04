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

import static org.codroid.editor.UIUtilsKt.dip2px;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import org.codroid.interfaces.addon.AddonManager;
import org.codroid.interfaces.appearance.AppearanceProperty;

/**
 * CodroidEditor is a core part of this project.
 * It contains the function of highlight, autocomplete and etc.
 */

public class CodroidEditor extends AppCompatEditText {

    private Configure mConfigure;

    private Paint lineNumberPaint, backPaint;

    private Rect lineNumberBackRect;


    public CodroidEditor(@NonNull Context context) {
        super(context);
        init();
    }

    public CodroidEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CodroidEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (mConfigure == null) mConfigure = new Configure();
        if (lineNumberPaint == null && mConfigure.isShowLineNumber()) {
            lineNumberPaint = new Paint();
            lineNumberPaint.setStyle(Paint.Style.FILL);
            lineNumberPaint.setTextSize(getTextSize());
            lineNumberPaint.setColor(mConfigure.getLineNumberColor());

            backPaint = new Paint();
            backPaint.setStyle(Paint.Style.FILL);
            backPaint.setColor(mConfigure.getLineNumberBackColor());

            lineNumberBackRect = new Rect();
        }
        if(AddonManager.get().getColor(AppearanceProperty.Attribute.EDITOR_BACKGROUND) != null) {
            setBackgroundColor(AddonManager.get().getColor(AppearanceProperty.Attribute.EDITOR_BACKGROUND).toArgb());
            Log.i("Zac", AddonManager.get().getColor(AppearanceProperty.Attribute.EDITOR_BACKGROUND).toArgb() + "");
        }
    }

    public Configure getConfigure() {
        return mConfigure;
    }

    public void setConfigure(Configure mConfigure) {
        this.mConfigure = mConfigure;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Drawing line number
        if (mConfigure.isShowLineNumber()) {
            int gap = dip2px(getContext(),4);
            int paddingLeft = (int) lineNumberPaint.measureText(String.valueOf(getLineCount())) + gap * 2;
            lineNumberBackRect.set(0, 0, paddingLeft, getHeight());
            setPadding(paddingLeft + gap, 0,0,0);
            canvas.drawRect(lineNumberBackRect, backPaint);
            for(int i = 0; i < getLineCount(); i ++){
                int baseLine = getLineBounds(i, null);
                canvas.drawText(String.valueOf(i + 1), gap, baseLine, lineNumberPaint);
            }
        }
    }


    public class Configure {
        private boolean lineNumber = true;
        private @ColorInt int lineNumberColor = Color.BLACK;
        private @ColorInt int lineNumberBackColor = Color.GRAY;

        public boolean isShowLineNumber() {
            return lineNumber;
        }

        public Configure showLineNumber(boolean lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public @ColorInt
        int getLineNumberColor() {
            return lineNumberColor;
        }

        public Configure setLineNumberColor(@ColorInt int lineNumberColor) {
            this.lineNumberColor = lineNumberColor;
            return this;
        }

        public @ColorInt int getLineNumberBackColor() {
            return lineNumberBackColor;
        }

        public Configure setLineNumberBackColor(@ColorInt int lineNumberBackColor) {
            this.lineNumberBackColor = lineNumberBackColor;
            return this;
        }
    }
}
