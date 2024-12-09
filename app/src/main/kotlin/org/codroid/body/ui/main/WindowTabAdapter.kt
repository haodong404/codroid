package org.codroid.body.ui.main

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import org.codroid.body.databinding.ItemWindowTabBinding
import org.codroid.body.widgets.WindowTab
import java.nio.file.Path

class WindowTabAdapter :
    BaseQuickAdapter<Path, DataBindingHolder<ItemWindowTabBinding>>() {

    var currentPosition = 0
    private var closeListener: ((Path, Int) -> Unit?)? = null

    fun setOnCloseListener(listener: (path: Path, position: Int) -> Unit) {
        this.closeListener = listener
    }

    fun select(position: Int) {
        notifyItemChanged(position)
        notifyItemChanged(currentPosition)
        this.currentPosition = position
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemWindowTabBinding>,
        position: Int,
        item: Path?
    ) {
        holder.binding.path = item
        (holder.binding.root as WindowTab).apply {
            setIsSelected(holder.layoutPosition == currentPosition)
            this.setOnCloseListener {
                item?.let { path ->
                    closeListener?.invoke(path, holder.layoutPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ItemWindowTabBinding> = DataBindingHolder(
        ItemWindowTabBinding.inflate(
            LayoutInflater.from(context),parent, false
        )
    )

}