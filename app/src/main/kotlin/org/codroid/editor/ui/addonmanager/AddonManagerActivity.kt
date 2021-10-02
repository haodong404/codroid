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

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import org.codroid.editor.databinding.ActivityAddonManagerBinding

class AddonManagerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddonManagerBinding

    private lateinit var adapter: AddonRecyclerAdapter

    private val viewModel: AddonManagerViewModel by viewModels()

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            viewModel.importAddon(it).observe(this) {
                if (!it) {
                    Snackbar.make(binding.root, "Import Failed", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(binding.root, "Import Succeed", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddonManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = AddonRecyclerAdapter()

        binding.activityAddonRv.layoutManager = LinearLayoutManager(this)
        binding.activityAddonRv.adapter = adapter
        binding.addonManagerFloatingActionButton.setOnClickListener(this)

        viewModel.listAddons().observe(this) {
            if (it.isSucceed()) {
                adapter.setList(it.result)
            } else {
                Snackbar.make(binding.root, it.errorMessage ?: "Error", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.addonManagerFloatingActionButton) {
            getContent.launch("application/java-archive")
        }
    }

}