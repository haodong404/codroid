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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.codroid.body.databinding.FragmentEditorWindowBinding
import java.nio.file.Path

class EditorWindowFragment(var path: Path) : Fragment() {

    private lateinit var binding: FragmentEditorWindowBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditorWindowBinding.inflate(inflater)
//        lifecycleScope.launchWhenCreated {
//            binding.editorWindowCodroidEditor.fileInput(path, Charsets.UTF_8)
//        }
        binding.editorWindowCodroidEditor.setText(
                "package org.codroid.editor.ui.mainpackage org.codroid.editor.ui.mainpackage org.codroid.editor.ui.mainpackage org.codroid.editor.ui.mainpackage org.codroid.editor.ui.mainpackage org.codroid.editor.ui.main\n" +
                "\n" +
                "import android.Manifest\n" +
                "import android.content.Intent\n" +
                "import android.os.Build\n" +
                "import android.os.Bundle\n" +
                "import android.view.Menu\n" +
                "import android.view.MenuItem\n" +
                "import androidx.activity.viewModels\n" +
                "import androidx.appcompat.app.AppCompatActivity\n" +
                "import androidx.lifecycle.lifecycleScope\n" +
                "import androidx.lifecycle.viewModelScope\n" +
                "import androidx.recyclerview.widget.LinearLayoutManager\n" +
                "import com.google.android.material.snackbar.Snackbar\n" +
                "import com.permissionx.guolindev.PermissionX\n" +
                "import kotlinx.coroutines.launch\n" +
                "import org.codroid.editor.Codroid\n" +
                "import org.codroid.editor.R\n" +
                "import org.codroid.editor.databinding.ActivityMainBinding\n" +
                "import org.codroid.editor.ui.addonmanager.AddonManagerActivity\n" +
                "import org.codroid.editor.ui.dirtree.FileTreeNode\n" +
                "import org.codroid.editor.ui.dirtree.DirTreeAdapter\n" +
                "import org.codroid.editor.ui.utils.EditorWindowHelper\n" +
                "import org.codroid.editor.widgets.DirTreeItemView\n" +
                "import java.nio.file.Paths\n" +
                "\n" +
                "\n" +
                "class MainActivity : AppCompatActivity() {\n" +
                "\n" +
                "    private lateinit var binding: ActivityMainBinding\n" +
                "\n" +
                "    private val mDirTreeAdapter by lazy {\n" +
                "        DirTreeAdapter()\n" +
                "    }\n" +
                "\n" +
                "    private val viewModel: MainViewModel by viewModels()\n" +
                "\n" +
                "    private lateinit var mWindowHelper: EditorWindowHelper\n" +
                "\n" +
                "    override fun onCreate(savedInstanceState: Bundle?) {\n" +
                "        super.onCreate(savedInstanceState)\n" +
                "        binding = ActivityMainBinding.inflate(layoutInflater)\n" +
                "        setContentView(binding.root)\n" +
                "\n" +
                "        setSupportActionBar(binding.activityMainToolbar)\n" +
                "\n" +
                "        permissionApply()\n" +
                "\n" +
                "        editorWindow()\n" +
                "\n" +
                "        dirTreeWindow()\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    private fun editorWindow() {\n" +
                "        val adapter = EditorWindowAdapter(supportFragmentManager, lifecycle)\n" +
                "        binding.activityMainEditorWindow.adapter = adapter\n" +
                "        val windowTagAdapter = WindowTabAdapter()\n" +
                "        binding.activityMainTabRv.adapter = windowTagAdapter\n" +
                "        binding.activityMainTabRv.itemAnimator = null\n" +
                "        binding.activityMainTabRv.layoutManager =\n" +
                "            LinearLayoutManager(this).apply { orientation = LinearLayoutManager.HORIZONTAL }\n" +
                "        mWindowHelper =\n" +
                "            EditorWindowHelper(binding.activityMainEditorWindow, binding.activityMainTabRv)\n" +
                "    }\n")
        return binding.root
    }

}