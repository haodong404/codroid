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
