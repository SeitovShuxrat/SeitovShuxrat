package com.example.landtech.presentation.ui.order_details.tab_fragments.spare_parts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.landtech.databinding.FragmentSparePartsBinding
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel

class SparePartsFragment : Fragment() {
    private lateinit var binding: FragmentSparePartsBinding
    private val viewModel: OrderDetailsViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSparePartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@SparePartsFragment.viewModel
            receivedPartsRecyclerView.adapter = ReceivedPartsListAdapter()
            returnedPartsRecyclerView.adapter = ReturnedPartsListAdapter()
        }
    }
}