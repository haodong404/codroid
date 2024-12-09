package org.codroid.body.ui.addonmanager

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import org.codroid.body.databinding.ActivityAddonManagerBinding

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
                it.result?.let { r -> adapter.addAll(r) }
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