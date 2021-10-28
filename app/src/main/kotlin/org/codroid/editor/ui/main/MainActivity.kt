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

package org.codroid.editor.ui.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.codroid.editor.R
import org.codroid.editor.databinding.ActivityMainBinding
import org.codroid.editor.ui.addonmanager.AddonManagerActivity
import org.codroid.editor.ui.projectstruct.ProjectStructureAdapter
import org.codroid.editor.ui.utils.isStoragePermissionGranted


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val PERMISSION_RESULT_CODE = 1

    private val projectStructAdapter by lazy {
        ProjectStructureAdapter()
    }
    private val viewModel: MainViewModel by viewModels()

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        it?.let {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!isStoragePermissionGranted(this)) showPermissionDialog() else loadFileList()
        setSupportActionBar(binding.activityMainToolbar)
        binding.projectStructureRv.adapter = projectStructAdapter
        binding.projectStructureRv.layoutManager = LinearLayoutManager(this)
        binding.activityMainBottomPanel.y = 100f

        org.codroid.interfaces.addon.AddonManager.get().logger.i("Hello Logger");
    }

    private fun permissionAsk() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).let {
                    it.data = Uri.parse("package:$packageName")
                    startActivityForResult(this, it, PERMISSION_RESULT_CODE, null)
                }
            }
        } else {
            if (!isStoragePermissionGranted(this)) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PERMISSION_RESULT_CODE
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_tool_bar_menu, menu)
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_RESULT_CODE) {
            if (!isStoragePermissionGranted(this)) onPermissionDenied() else loadFileList()
        }
    }

    private fun onPermissionDenied() {
        Snackbar.make(
            binding.root,
            getString(R.string.storage_permission_denied),
            Snackbar.LENGTH_SHORT
        ).show()
        showPermissionDialog()
    }

    private fun showPermissionDialog() {
        MaterialAlertDialogBuilder(this)
            .setMessage(getString(R.string.permission_dialog_message))
            .setCancelable(false)
            .setPositiveButton("Accept") { _, _ ->
                permissionAsk()
            }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_RESULT_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) onPermissionDenied() else loadFileList()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_plugin_manager -> {
            startActivity(Intent(this, AddonManagerActivity::class.java))
            true
        }

        R.id.action_setting -> {

            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun loadFileList(){
        viewModel.listDir("/mnt/sdcard").observe(this@MainActivity) {
            projectStructAdapter.setList(it)
        }
    }
}