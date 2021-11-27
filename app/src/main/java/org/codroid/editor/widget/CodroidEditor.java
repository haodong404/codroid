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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import org.codroid.editor.R;
import org.codroid.interfaces.addon.AddonManager;
import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.appearance.Part;
import org.codroid.interfaces.appearance.editor.WrappedSpannable;
import org.codroid.interfaces.appearance.parts.EditorPart;
import org.codroid.interfaces.evnet.EventCenter;
import org.codroid.interfaces.evnet.editor.SelectionChangedEvent;
import org.codroid.interfaces.evnet.editor.TextChangedEvent;

import java.util.Optional;

/**
 * CodroidEditor is a core part of this project.
 * It contains the function of highlight, autocomplete and etc.
 */

public class CodroidEditor extends AppCompatEditText {

    private Configure mConfigure;
    private WrappedSpannable mWrappedSpannable;

    private Paint lineNumberPaint, backPaint;

    private Rect lineNumberBackRect;

    // This is suggestion window
    private PopupWindow mPopupWindow;

    // The part of editor that defines the appearances
    private Optional<Part> part;

    private int paddingLeft;
    private int gap;

    // return true if editor is drawn at first time.
    private boolean isDrawn = false;

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

            part = AddonManager.get().appearancePart(AppearanceProperty.PartEnum.EDITOR);

            part.ifPresent(p -> p.findColor(EditorPart.Attribute.BACKGROUND,
                    value -> setBackgroundColor(value.toArgb())));
            lineNumberBackRect = new Rect();
        }
        mWrappedSpannable = new WrappedSpannable(getText());

    }

    private void initSuggestionWindow() {
        if (mPopupWindow == null) {
            View view = View.inflate(getContext(), R.layout.suggestion_window, null );
            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, 300);
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        gap = (int) dip2px(getContext(), 4);
        paddingLeft = (int) lineNumberPaint.measureText(String.valueOf(getLineCount())) + gap * 2;
        setPadding(paddingLeft + gap, 0, 0, 0);

    }

    public Configure getConfigure() {
        return mConfigure;
    }

    public void setConfigure(Configure mConfigure) {
        this.mConfigure = mConfigure;
    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        AddonManager.get().eventCenter()
                .<SelectionChangedEvent>execute(EventCenter.EventsEnum.EDITOR_SELECTION_CHANGED)
                .forEach(it -> {
                    try {
                        it.onSelectionChanged(mWrappedSpannable.update(getText()), selStart, selEnd);
                    } catch (Exception e) {
                        AddonManager.get().getLogger().e("Event editor_selection_changed executes failed with: " + e);
                    }
                });
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        AddonManager.get().eventCenter()
                .<TextChangedEvent>execute(EventCenter.EventsEnum.EDITOR_TEXT_CHANGED)
                .forEach(it -> {
                    try {
                        it.onTextChanged(mWrappedSpannable.update(getText()), start, lengthBefore, lengthAfter);
                    } catch (Exception e) {
                        AddonManager.get().getLogger().e("Event editor_text_changed executes failed with: " + e);
                    }
                });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isDrawn) isDrawn = true;
        //Drawing line number
        if (mConfigure.isShowLineNumber()) {
            lineNumberBackRect.set(0, 0, paddingLeft, getHeight());
            canvas.drawRect(lineNumberBackRect, backPaint);
            for (int i = 0; i < getLineCount(); i++) {
                int baseLine = getLineBounds(i, null);
                canvas.drawText(String.valueOf(i + 1), gap, baseLine, lineNumberPaint);
            }
        }
    }

    public void showSuggestionWindow() {
        initSuggestionWindow();
        mPopupWindow.dismiss();
        mPopupWindow.showAtLocation(this, Gravity.TOP | Gravity.START, 0,
                getLineBounds(getCurrentCursorLine() - 1, null) + mPopupWindow.getHeight() + getLineHeight() + 30);
    }

    private int getCurrentCursorLine() {
        int selectionStart = getSelectionStart();
        if (selectionStart != -1) {
            return getLayout().getLineForOffset(selectionStart) + 1;
        }
        return -1;
    }


    public class Configure {
        private boolean lineNumber = true;
        private @ColorInt
        int lineNumberColor = Color.BLACK;
        private @ColorInt
        int lineNumberBackColor = Color.GRAY;

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

        public @ColorInt
        int getLineNumberBackColor() {
            return lineNumberBackColor;
        }

        public Configure setLineNumberBackColor(@ColorInt int lineNumberBackColor) {
            this.lineNumberBackColor = lineNumberBackColor;
            return this;
        }
    }
}
