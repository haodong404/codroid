package org.codroid.body.ui.addonmanager

import android.net.Uri
import android.provider.DocumentsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.codroid.body.Codroid
import org.codroid.body.ui.AddonItem
import org.codroid.body.ui.Response
import org.codroid.interfaces.addon.AddonDescription
import org.codroid.interfaces.addon.AddonLoader
import org.codroid.interfaces.addon.AddonManager
import org.codroid.interfaces.utils.PathUtils
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
            AddonManager.get().addonDao.findAll()?.mapNotNull {
                try {
                    AddonLoader.findAddonDescription(
                        PathUtils.splice(
                            it.addonPath,
                            AddonDescription.ADDON_DESCRIPTION_FILE_NAME
                        )
                    ).get()
                } catch (e: Exception) {
                    null
                }
            }?.map {
                AddonItem(
                    it.name,
                    it.versionDes,
                    it.description,
                    it.author,
                    it.link
                )
            }?.apply {
                addonSet.postValue(Response(Response.SUCCEED, this))
            }
        }
        return addonSet
    }

    fun importAddon(addonUri: Uri): LiveData<Boolean> {
        viewModelScope.launch {
            getRealPath(addonUri)?.let { it ->
                File(it).let {
                    AddonManager.get().importExternalAddonAsync(it, object: AddonManager.ProgressCallback<AddonManager.ImportStage, String> {
                        override fun progress(
                            total: Int,
                            now: Int,
                            stage: AddonManager.ImportStage?,
                            attachment: String?
                        ) {

                        }

                        override fun done() {
                            importAddon.postValue(true)
                        }

                        override fun error(e: Throwable?) {
                            importAddon.postValue(false)
                        }

                    })
                }
            }
        }
        return importAddon
    }

    private fun getRealPath(uri: Uri): String? {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":")
        val type = split[0]
        if ("primary".contentEquals(type)) {
            return "${Codroid.SDCARD_ROOT_DIR}/${split[1]}"
        }
        return null
    }
}