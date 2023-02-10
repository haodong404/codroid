package org.codroid.body.ui.main

import android.util.Log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import org.codroid.body.R
import org.codroid.body.databinding.ItemStatusInfoBinding
import org.codroid.body.widgets.StatusTagData

class StatusInfoAdapter :
    BaseQuickAdapter<StatusTagData, BaseDataBindingHolder<ItemStatusInfoBinding>>(R.layout.item_status_info) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemStatusInfoBinding>,
        item: StatusTagData
    ) {
        holder.dataBinding?.run {
            val tag = itemStatusInfoTag
            item.text?.let {
                tag.setText(it)
            }
            item.icon?.let {
                tag.setBitmap(it)
            }
        }
    }
}