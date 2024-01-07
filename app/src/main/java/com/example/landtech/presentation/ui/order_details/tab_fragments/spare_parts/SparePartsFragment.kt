package com.example.landtech.presentation.ui.order_details.tab_fragments.spare_parts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.landtech.databinding.FragmentSparePartsBinding
import com.example.landtech.presentation.ui.order_details.OrderDetailsFragmentDirections
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
            receivedPartsRecyclerView.adapter = ReceivedPartsListAdapter {
                this@SparePartsFragment.viewModel.setModified(true)
            }

            this@SparePartsFragment.viewModel.isMainUser.observe(viewLifecycleOwner) {
                setEnabled(it)
            }

            val returnedPartsAdapter =
                ReturnedPartsListAdapter(
                    onReturnedQuantityChanged = { item, quantity ->
                        this@SparePartsFragment.viewModel.setReturnedItemQuantity(item, quantity)
                    },
                    onDeleteClickListener = {
                        this@SparePartsFragment.viewModel.deleteReturnedSparePart(it)
                    })
            returnedPartsRecyclerView.adapter = returnedPartsAdapter

            addReturnPart.setOnClickListener {
                findNavController().navigate(OrderDetailsFragmentDirections.actionOrderDetailsFragmentToSelectReceivedPartFragment())
            }

            createTransferDocs.setOnClickListener {
                findNavController().navigate(OrderDetailsFragmentDirections.actionOrderDetailsFragmentToTransferOrdersListFragment())
            }
        }
    }

    private fun setEnabled(isMainUser: Boolean?) {
        isMainUser?.let {
            binding.apply {
                receivedPartsRecyclerView.isEnabled = it
                returnedPartsRecyclerView.isEnabled = it
                createTransferDocs.isEnabled = it
                addReturnPart.isEnabled = it
            }
        }
    }
}