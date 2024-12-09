package org.codroid.body.ui.utils

import android.content.Context
import android.widget.Toast

fun Context.shortToast(content: CharSequence){
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
}

fun Context.longToast(content: CharSequence){
    Toast.makeText(this, content, Toast.LENGTH_LONG).show()
}
