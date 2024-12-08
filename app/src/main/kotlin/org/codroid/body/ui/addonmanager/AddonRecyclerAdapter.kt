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