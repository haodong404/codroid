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

import android.content.Context
import android.net.Uri
import android.os.Environment.getExternalStorageDirectory
import android.provider.DocumentsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.codroid.editor.addon.AddonLoader
import org.codroid.editor.addon.AddonManager
import org.codroid.editor.ui.AddonItem
import org.codroid.editor.ui.Response
import java.io.File

class AddonManagerViewModel : ViewModel() {

    private val addonSet by lazy {
        MutableLiveData<Response<List<AddonItem>>>()
    }

    private val importAddon by lazy {
        MutableLiveData<Boolean>()
    }

    fun listAddons(): LiveData<Response<List<AddonItem>>> {
        viewModelScope.launch {
            AddonManager.get().loadedAddons()?.keys?.map {
                AddonItem(
                    it.get().name,
                    it.get().versionDes,
                    it.get().description,
                    it.get().author,
                    it.get().link
                )
            }?.apply {
                addonSet.postValue(Response(Response.SUCCEED, this))
            }
        }
        return addonSet
    }

    fun importAddon(context: Context, addonUri: Uri): LiveData<Boolean> {
        viewModelScope.launch {
            getRealPath(addonUri)?.let {
                val file = File(it)
                AddonManager.get().importExternalAddon(context, file).let { it2 ->
                    importAddon.postValue(it2.isSucceed)
                }
            }
        }
        return importAddon
    }

    private fun getRealPath(uri: Uri): String? {
        val docId = DocumentsContract.getDocumentId(uri);
        val split = docId.split(":");
        val type = split[0];
        if ("primary".contentEquals(type)) {
            return "${getExternalStorageDirectory()}/${split[1]}";
        }
        return null
    }
}