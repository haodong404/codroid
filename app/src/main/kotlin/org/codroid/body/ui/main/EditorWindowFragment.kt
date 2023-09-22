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
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.codroid.body.databinding.FragmentEditorWindowBinding
import org.codroid.interfaces.addon.AddonManager
import java.nio.file.Path

class EditorWindowFragment(var path: Path) : Fragment() {

    private lateinit var binding: FragmentEditorWindowBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditorWindowBinding.inflate(inflater)
        binding.editorWindowCodroidEditor.loadAsync(path) {
            if (it != null) {
                Toast.makeText(context, "Cannot open file: $path", Toast.LENGTH_SHORT).show()
                AddonManager.get().logger.e(it.stackTraceToString())
            }
        }
        return binding.root
    }

    fun save() {
        binding.editorWindowCodroidEditor.saveAsync(path) {
            if (it == null) {
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
            } else {
                AddonManager.get().logger.e(it.stackTraceToString())
                Toast.makeText(context, "Save failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun insetText(text: CharSequence) {
        binding.editorWindowCodroidEditor.insertText(text)
    }
}