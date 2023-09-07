package com.example.landtech.presentation.ui.orders

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.landtech.databinding.FragmentOrdersBinding

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