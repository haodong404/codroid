package org.codroid.body.ui.addonmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import org.codroid.body.databinding.ItemAddonBinding
import org.codroid.body.ui.AddonItem

class AddonRecyclerAdapter :
    BaseQuickAdapter<AddonItem, DataBindingHolder<ItemAddonBinding>>() {

    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemAddonBinding>,
        position: Int,
        item: AddonItem?
    ) {
        holder.binding.item = item
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ItemAddonBinding> = DataBindingHolder(
        ItemAddonBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
    )
}