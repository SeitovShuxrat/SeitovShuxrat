package com.example.landtech.presentation.ui.select_spare_part

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.landtech.R
import com.example.landtech.databinding.FragmentSelectSparePartBinding
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel
import com.example.landtech.presentation.ui.used_parts_add.UsedPartAddFragment

class SelectSparePartFragment : Fragment() {

    private val viewModel: OrderDetailsViewModel by activityViewModels()
    private lateinit var binding: FragmentSelectSparePartBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectSparePartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchSparePartList()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            val adapter = SelectSparePartAdapter(SelectSparePartAdapter.OnClickListener {
                if (findNavController().previousBackStackEntry?.destination?.id == R.id.usedPartAddFragment) {
                    viewModel.setSparePartUsedPartAdd(it)
                    findNavController().navigateUp()
                } else if (findNavController().previousBackStackEntry?.destination?.id == R.id.addSparePartDetailFragment) {
                    viewModel.setNewSparePartItem(it)
                    findNavController().navigateUp()
                }
            })

            selectSparePartRecyclerView.adapter = adapter

            this@SelectSparePartFragment.viewModel.sparePartsList.observe(viewLifecycleOwner) {
                adapter.submitList(it)

                errorMsg.visibility = if (it.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }

    }
}