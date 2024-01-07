package com.example.landtech.presentation.ui.used_parts_add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.landtech.databinding.FragmentAddUsedPartBinding
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel


class UsedPartAddFragment : Fragment() {

    private val viewModel: OrderDetailsViewModel by activityViewModels()
    private lateinit var binding: FragmentAddUsedPartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddUsedPartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chooseWarehouseItems = arrayListOf("UHM LT склад", "Клиент")
        val warehouseDataAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                chooseWarehouseItems
            )
        warehouseDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            viewModel.usedPartItemAdd.observe(viewLifecycleOwner) {
                when (it?.clientUhm) {
                    "UHM LT склад" -> {
                        selectWarehouseSpinner.setSelection(0)
                        alterVisibilityUhm(true)
                    }

                    "Клиент" -> {
                        selectWarehouseSpinner.setSelection(1)
                        alterVisibilityUhm(false)
                    }

                    else -> Unit
                }

                usedPart = it
            }

            selectWarehouseSpinner.apply {

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        viewModel.changeClientUhm(parent?.getItemAtPosition(position).toString())

                        if (position == 0) {
                            alterVisibilityUhm(true)
                        } else {
                            alterVisibilityUhm(false)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

                quantity.addTextChangedListener(viewModel.addUsedItemQuantityWatcher)


                adapter = warehouseDataAdapter
            }

            selectGoodsBtn.setOnClickListener {
                findNavController().navigate(UsedPartAddFragmentDirections.actionUsedPartAddFragmentToSelectSparePartFragment(true))
            }

            cancelBtn.setOnClickListener {
                viewModel.clearUsedPartAdd()
                findNavController().navigateUp()
            }

            saveBtn.setOnClickListener {

                viewModel.apply {
                    if (usedPartAddFieldsAreSet()) {
                        addUsedPartItem()
                        clearUsedPartAdd()
                    } else {
                        Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_LONG)
                            .show()
                        return@setOnClickListener
                    }
                }

                findNavController().navigateUp()
            }

            numberET.addTextChangedListener {
                viewModel.setUsedPartAddNumber(it.toString())
            }
        }
    }

    private fun alterVisibilityUhm(setVisible: Boolean) {
        val visibility = if (setVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.apply {
            numberLabel.visibility = visibility
            number.visibility = visibility
            nameLabel.visibility = visibility
            name.visibility = visibility
            codeLabel.visibility = visibility
            code.visibility = visibility
            selectGoodsBtn.visibility = visibility
            numberET.visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }
}