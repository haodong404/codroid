package org.codroid.body.widgets

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.forEach
import org.codroid.body.databinding.ViewActionItemBinding


private class ActionItem : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val mBinding = ViewActionItemBinding.inflate(LayoutInflater.from(context), this, true)

    fun setData(item: MenuItem) {
        mBinding.viewActionItemTv.text = item.title ?: "Null"
    }
}

class ActionBar : LinearLayout {

    @SuppressLint("RestrictedApi")
    private val menu: Menu = MenuBuilder(context);

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun bind(activity: Activity) {
        activity.onCreateOptionsMenu(menu)
        menu.forEach {
            addView(ActionItem(context).apply {
                setData(it)
            })
        }
    }
}