package org.codroid.body.ui.buttompannel

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import org.codroid.body.databinding.ItemSymbolBinding
import org.codroid.body.ui.SymbolItem

class ConvinientSymbolsAdapter :
    BaseQuickAdapter<SymbolItem, DataBindingHolder<ItemSymbolBinding>>() {

    class ViewHolder(
        parent: ViewGroup, val binding: ItemSymbolBinding = ItemSymbolBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(parent)

    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemSymbolBinding>,
        position: Int,
        item: SymbolItem?
    ) {
        holder.binding.item = item
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ItemSymbolBinding> = DataBindingHolder(ItemSymbolBinding.inflate(
        LayoutInflater.from(context), parent, false))

}