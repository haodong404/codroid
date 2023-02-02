package org.codroid.body.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.codroid.body.R
import org.codroid.body.databinding.ViewInfoBarBinding

class InfoBar : ConstraintLayout {

    private val mBinding: ViewInfoBarBinding =
        ViewInfoBarBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {
        val tv = context.obtainStyledAttributes(attrs, R.styleable.InfoBar)
        mBinding.title = tv.getString(R.styleable.InfoBar_android_title) ?: "Title"
        mBinding.subtitle = tv.getString(R.styleable.InfoBar_android_subtitle) ?: "Subtitle"
        tv.recycle()
    }
}