package com.example.landtech.presentation.ui.signature_screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.landtech.R
import com.example.landtech.databinding.FragmentSignatureBinding
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import okio.IOException

@AndroidEntryPoint
class SignatureFragment : Fragment() {

    private val viewModel: OrderDetailsViewModel by activityViewModels()
    private lateinit var binding: FragmentSignatureBinding
    private lateinit var fileName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.order.value?.id.let {
            fileName = "sign_$it"
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    saveBitmap(fileName, binding.signatureView.signatureBitmap)
                    findNavController().navigateUp()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignatureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                saveBitmap(fileName, binding.signatureView.signatureBitmap)
                return false
            }
        }, viewLifecycleOwner)

        viewModel.order.value?.id.let {
            val fileArray = requireContext().filesDir.listFiles { _, name ->
                name == "$fileName.jpg"
            }

            val file = if ((fileArray?.size ?: 0) > 0) fileArray?.get(0) else null

            file?.let {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    .copy(Bitmap.Config.ARGB_8888, true)
                binding.signatureView.setBitmap(bmp)
            }
        }

        binding.apply {
            clearBtn.setOnClickListener {
                signatureView.clearCanvas()
            }
            clientRejectedToSign.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.clientRejectedToSign(isChecked)

                if (isChecked)
                    findNavController().navigateUp()
            }
        }
    }

    fun saveBitmap(fileName: String, bitmap: Bitmap): Boolean {
        if (binding.signatureView.isBitmapEmpty) return true

        return try {
            requireContext().openFileOutput("$fileName.jpg", Context.MODE_PRIVATE).use {
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it)) {
                    throw IOException("Couldn't save bitmap!")
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}