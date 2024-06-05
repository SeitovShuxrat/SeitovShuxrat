package com.example.landtech.presentation.ui.select_spare_part

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.landtech.R
import com.example.landtech.databinding.FragmentSelectSparePartBinding
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel

class SelectSparePartFragment : Fragment() {

    private val viewModel: OrderDetailsViewModel by activityViewModels()
    private lateinit var binding: FragmentSelectSparePartBinding
    private val args: SelectSparePartFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectSparePartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchSparePartList(args.showOnlyRemainders, args.orderId)

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.select_spare_part_menu, menu)

                val menuItem = menu.findItem(R.id.search)
                val searchView = menuItem.actionView as SearchView
                searchView.queryHint = "Поиск"

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let { viewModel.setSparePartsListSearch(it) }
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner)

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

    override fun onDestroy() {
        viewModel.clearSparePartsList()
        super.onDestroy()
    }
}