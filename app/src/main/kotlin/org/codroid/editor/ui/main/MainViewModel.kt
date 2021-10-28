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

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.codroid.editor.R
import org.codroid.editor.ui.FileItem
import java.io.File

class MainViewModel : ViewModel() {

    private val fileList: MutableLiveData<List<FileItem>> by lazy {
        MutableLiveData<List<FileItem>>()
    }

    fun listDir(pathname: String): LiveData<List<FileItem>> {
        viewModelScope.launch(Dispatchers.Default) {
            File(pathname).apply {
                val fileItems: MutableList<FileItem> = mutableListOf()
                if (this.exists()) {
                    list()?.asSequence()?.forEach { it ->
                        fileItems.add(FileItem(it, R.drawable.ic_launcher_background, Color.BLACK))
                    }
                    fileList.postValue(fileItems)
                }
            }
        }
        return fileList
    }
}