package com.example.landtech.presentation.ui.engineers_select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.landtech.databinding.FragmentEngineersSelectBinding
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EngineersSelectFragment : Fragment() {

    private lateinit var binding: FragmentEngineersSelectBinding
    private val viewModel: OrderDetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEngineersSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            val adapter = EngineersSelectListAdapter(EngineersSelectListAdapter.OnClickListener {
                viewModel.addEngineer(it)
                Toast.makeText(
                    requireContext(), "Инженер добавлен в список!", Toast.LENGTH_LONG
                ).show()
                findNavController().navigateUp()
            })
            engineersRecyclerView.adapter = adapter

            viewModel.getEngineersList().observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }
    }
}