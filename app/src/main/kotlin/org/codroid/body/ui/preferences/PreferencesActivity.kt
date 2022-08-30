package org.codroid.body.ui.preferences

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewCompat
import org.codroid.body.databinding.ActivityPreferencesBinding

class PreferencesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreferencesBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(this)
        Log.i("Zac", "WebView version: ${webViewPackageInfo?.versionName}")
        binding.preferencesWebview.settings.run {
            javaScriptEnabled = true
        }
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        binding.preferencesWebview.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val interceptedWebRequest = assetLoader.shouldInterceptRequest(request!!.url)
                interceptedWebRequest?.let {
                    if (request.url.toString().endsWith("js", true)) {
                        it.mimeType = "text/javascript"
                    }
                }
                return interceptedWebRequest
            }
        }
        binding.preferencesWebview.addJavascriptInterface(PreferencesInjection(), "PreferencesInjection")
        binding.preferencesWebview.loadUrl("https://appassets.androidplatform.net/assets/preferences-ui/index.html")
    }
}