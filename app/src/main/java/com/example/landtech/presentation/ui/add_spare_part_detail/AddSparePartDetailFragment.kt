package com.example.landtech.presentation.ui.add_spare_part_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.landtech.databinding.FragmentAddSparePartDetailBinding
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel

class AddSparePartDetailFragment : Fragment() {

    private val viewModel: OrderDetailsViewModel by activityViewModels()
    private lateinit var binding: FragmentAddSparePartDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddSparePartDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            quantity.addTextChangedListener(viewModel.newSparePartQuantityWatcher)

            viewModel.newAddedSparePart.observe(viewLifecycleOwner) {
                usedPart = it
            }

            selectGoodsBtn.setOnClickListener {
                findNavController().navigate(AddSparePartDetailFragmentDirections.actionAddSparePartDetailFragmentToSelectSparePartFragment(false))
            }

            cancelBtn.setOnClickListener {
                viewModel.clearNewSparePart()
                findNavController().navigateUp()
            }

            saveBtn.setOnClickListener {
                viewModel.addNewSparePartToList()
                findNavController().navigateUp()
            }
        }
    }
}