package com.example.landtech.presentation.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.landtech.R
import com.example.landtech.databinding.FragmentOrdersBinding
import com.example.landtech.domain.models.OrderStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi


@AndroidEntryPoint
class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private val viewModel: OrdersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            show()
            setHomeButtonEnabled(false)
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowHomeEnabled(false)
        }


        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.orders_list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_good_remainders -> {
                        findNavController().navigate(
                            OrdersFragmentDirections.actionOrdersFragmentToSelectSparePartFragment(
                                true,
                                null
                            )
                        )
                        true
                    }

                    R.id.menu_all -> {
                        viewModel.setStatusFilter(null)
                        true
                    }

                    R.id.menu_on_negotiation -> {
                        viewModel.setStatusFilter(OrderStatus.NEW)
                        true
                    }

                    R.id.menu_being_done -> {
                        viewModel.setStatusFilter(OrderStatus.IN_WORK)
                        true
                    }

                    R.id.menu_ended -> {
                        viewModel.setStatusFilter(OrderStatus.ENDED)
                        true
                    }

                    R.id.menu_closed -> {
                        viewModel.setStatusFilter(OrderStatus.CLOSED)
                        true
                    }

                    R.id.menu_logout -> {
                        viewModel.logout {
                            findNavController().navigate(OrdersFragmentDirections.actionOrdersFragmentToLoginFragment())
                        }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)

        viewModel.userLoggedIn.observe(viewLifecycleOwner) {
            if (it == false || it == null) findNavController().navigateUp()
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@OrdersFragment.viewModel
            ordersList.adapter = OrdersListAdapter(OrdersListAdapter.OnClickListener {
                findNavController().navigate(
                    OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(
                        it
                    )
                )
            })
        }
    }
}