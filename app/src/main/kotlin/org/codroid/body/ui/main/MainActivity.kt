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

package org.codroid.body.ui.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.launch
import org.codroid.body.Codroid
import org.codroid.body.R
import org.codroid.body.databinding.ActivityMainBinding
import org.codroid.body.ui.addonmanager.AddonManagerActivity
import org.codroid.body.ui.dirtree.FileTreeNode
import org.codroid.body.ui.dirtree.DirTreeAdapter
import org.codroid.body.ui.utils.EditorWindowHelper
import org.codroid.body.widgets.DirTreeItemView
import java.nio.file.Paths


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mDirTreeAdapter by lazy {
        DirTreeAdapter()
    }

    private val viewModel: MainViewModel by viewModels()

    private lateinit var mWindowHelper: EditorWindowHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.activityMainToolbar)

        permissionApply()

        editorWindow()

        dirTreeWindow()

        mWindowHelper.newWindow(Paths.get("/mnt/sdcard/Download/img.png"))
    }

    private fun editorWindow() {
        val adapter = EditorWindowAdapter(supportFragmentManager, lifecycle)
        binding.activityMainEditorWindow.adapter = adapter
        val windowTagAdapter = WindowTabAdapter()
        binding.activityMainTabRv.adapter = windowTagAdapter
        binding.activityMainTabRv.itemAnimator = null
        binding.activityMainTabRv.layoutManager =
            LinearLayoutManager(this).apply { orientation = LinearLayoutManager.HORIZONTAL }
        mWindowHelper =
            EditorWindowHelper(binding.activityMainEditorWindow, binding.activityMainTabRv)
    }

    private fun dirTreeWindow() {
        binding.activityMainDirTreeRv.adapter = mDirTreeAdapter
        binding.activityMainDirTreeRv.layoutManager = LinearLayoutManager(this)
        mDirTreeAdapter.setOnItemClickListener { adapter, view, position ->
            val now = adapter.getItem(position) as FileTreeNode
            val item = view.findViewById<DirTreeItemView>(R.id.dir_tree_item)
            item.changeStatus()
            if (item.isDir() && item.isExpanded) {
                viewModel.nextDir(now).observe(this@MainActivity) { response ->
                    response?.let {
                        mDirTreeAdapter.expand(position, it)
                    }
                }
            } else if (item.isDir() && !item.isExpanded) {
                mDirTreeAdapter.close(position)
            } else if (!item.isDir()) {
                lifecycleScope.launch {
                    now.element?.let {
                        val temp = it.toPath()
                        mWindowHelper.newWindow(temp)
                        mWindowHelper.change(temp)
                    }
                }
            }
        }
    }


    private fun permissionApply() {
        var permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT >= 30) {
            permissions = listOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }


        PermissionX.init(this)
            .permissions(permissions)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    getString(R.string.permission_dialog_message),
                    getString(R.string.dialog_accept),
                    getString(R.string.dialog_cancel)
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    viewModel.openDir(Codroid.SDCARD_ROOT_DIR)
                        .observe(this@MainActivity) { response ->
                            response?.let {
                                mDirTreeAdapter.setList(response)
                            }
                        }
                } else {
                    permissionApply()
                    onPermissionDenied()
                }
            }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_tool_bar_menu, menu)
        return true
    }

    private fun onPermissionDenied() {
        Snackbar.make(
            binding.root,
            getString(R.string.storage_permission_denied),
            Snackbar.LENGTH_SHORT
        ).show()

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_plugin_manager -> {
            startActivity(Intent(this, AddonManagerActivity::class.java))
            true
        }

        R.id.action_setting -> {
            binding.activityMainBottomPanel.close()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}