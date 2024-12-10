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