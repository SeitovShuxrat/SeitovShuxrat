package com.example.landtech.presentation.ui.add_spare_parts_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.landtech.R
import com.example.landtech.databinding.FragmentAddSparePartsListBinding
import com.example.landtech.databinding.FragmentAddUsedPartBinding
import com.example.landtech.presentation.ui.engineers_select.EngineersSelectListAdapter
import com.example.landtech.presentation.ui.order_details.OrderDetailsFragmentArgs
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel

class AddSparePartsListFragment : Fragment() {

    private val viewModel: OrderDetailsViewModel by activityViewModels()
    private lateinit var binding: FragmentAddSparePartsListBinding
    private val args: AddSparePartsListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddSparePartsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            val adapter = AddSparePartsListAdapter(
                AddSparePartsListAdapter.OnClickListener(onClickListener = {},
                    onDeleteClickListener = {
                        viewModel.deleteNewAddedSparePart(it)
                    })
            )
            sparePartsRecyclerView.adapter = adapter

            viewModel.newAddedSpareParts.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

            addBtn.setOnClickListener {
                viewModel.onNavigateToAddNewSparePart()
                findNavController().navigate(AddSparePartsListFragmentDirections.actionAddSparePartsListFragmentToAddSparePartDetailFragment())
            }
        }

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.orders_details_appbar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_save -> {
                        viewModel.saveNewSparePartsList(args.isDiagnose)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }
}