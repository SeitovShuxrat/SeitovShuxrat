package com.example.landtech.presentation.ui.order_details.tab_fragments.machinery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.landtech.databinding.FragmentMachineryBinding
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MachineryFragment : Fragment() {
    private val viewModel: OrderDetailsViewModel by activityViewModels()
    private lateinit var binding: FragmentMachineryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMachineryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@MachineryFragment.viewModel
        }
    }
}