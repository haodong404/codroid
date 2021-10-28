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

package org.codroid.editor.ui.addonmanager

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import org.codroid.editor.R
import org.codroid.editor.databinding.ItemAddonBinding
import org.codroid.editor.ui.AddonItem

class AddonRecyclerAdapter: BaseQuickAdapter<AddonItem, BaseDataBindingHolder<ItemAddonBinding>>(R.layout.item_addon) {

    override fun convert(holder: BaseDataBindingHolder<ItemAddonBinding>, item: AddonItem) {
        holder.dataBinding?.item = item
    }
}