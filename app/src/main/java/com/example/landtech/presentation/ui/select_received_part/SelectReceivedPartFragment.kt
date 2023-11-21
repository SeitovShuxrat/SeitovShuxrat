package com.example.landtech.presentation.ui.select_received_part

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.landtech.databinding.FragmentSelectReceivedPartBinding
import com.example.landtech.databinding.FragmentSelectSparePartBinding
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel

class SelectReceivedPartFragment : Fragment() {

    private val viewModel: OrderDetailsViewModel by activityViewModels()
    private lateinit var binding: FragmentSelectReceivedPartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectReceivedPartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@SelectReceivedPartFragment.viewModel

            val adapter = SelectReceivedPartListAdapter {
                this@SelectReceivedPartFragment.viewModel.addReturnedPart(it)
                findNavController().popBackStack()
            }

            receivedPartsRecyclerView.adapter = adapter
        }
    }
}