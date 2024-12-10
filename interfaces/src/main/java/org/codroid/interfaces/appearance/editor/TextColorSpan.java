package org.codroid.interfaces.appearance.editor;

import android.graphics.Color;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;

public abstract class TextColorSpan extends ForegroundColorSpan {

    public TextColorSpan(int color) {
        super(color);
    }

    public TextColorSpan(){
        super(0);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint textPaint) {
        textPaint.setColor(getColor().toArgb());
    }

    protected abstract Color getColor();

    public TextColorSpan(@NonNull Parcel src) {
        super(src);
    }
}
