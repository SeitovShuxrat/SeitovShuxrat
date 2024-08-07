package com.example.landtech.presentation.ui.order_details.tab_fragments.work

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.landtech.databinding.FragmentWorkBinding
import com.example.landtech.presentation.ui.engineers_select.EngineersSelectListAdapter
import com.example.landtech.presentation.ui.order_details.OrderDetailsFragmentDirections
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkFragment : Fragment() {
    private lateinit var binding: FragmentWorkBinding
    private val viewModel: OrderDetailsViewModel by activityViewModels()


//    private val startSpeechToText =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//
//            val resultCode = result.resultCode
//            val data = result.data
//
//            when (resultCode) {
//                Activity.RESULT_OK -> {
//                    val textArr = data?.getStringArrayListExtra(
//                        RecognizerIntent.EXTRA_RESULTS
//                    )
//                    binding.quickReport.setText(Objects.requireNonNull(textArr)?.get(0))
//                }
//
//                else -> {
//                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@WorkFragment.viewModel
            servicesRecyclerView.adapter =
                ServicesListAdapter(
                    this@WorkFragment.viewModel.worksHaveEnded.value ?: false,
                    onQuantityChanged = {
                        this@WorkFragment.viewModel.setModified(true)
                    },
                    onAutoGnClicked = { serviceItem ->
                        this@WorkFragment.viewModel.setCurrentServiceItem(serviceItem)
                        findNavController().navigate(
                            OrderDetailsFragmentDirections.actionOrderDetailsFragmentToExploitationObjectSelectFragment(
                                true
                            )
                        )
                    })
            usedSparePartsRecyclerView.adapter = UsedPartsListAdapter {
                this@WorkFragment.viewModel.deleteUsedSparePart(it)
            }

            this@WorkFragment.viewModel.isMainUser.observe(viewLifecycleOwner) {
                setEnabled(it)
            }
            this@WorkFragment.viewModel.worksHaveEnded.observe(viewLifecycleOwner) {
                if (it) setWorksEnded()
            }

            addEngineer.setOnClickListener {
                findNavController().navigate(OrderDetailsFragmentDirections.actionOrderDetailsFragmentToEngineersSelectFragment())
            }

            startWorkBtn.setOnClickListener {
                setAutoDriveTime()
            }

            endWorkBtn.setOnClickListener {
                setAutoDriveTime(true)
            }

            createGuaranteeOrder.setOnClickListener {
                this@WorkFragment.viewModel.setCreateGuaranteeOrder()
            }

            workNotGuaranteed.setOnClickListener {
                this@WorkFragment.viewModel.setWorkNotGuaranteed()
            }

            addUsedPart.setOnClickListener {
                this@WorkFragment.viewModel.onNavigateToAddUsedPart()
                findNavController().navigate(OrderDetailsFragmentDirections.actionOrderDetailsFragmentToUsedPartAddFragment())
            }

            addZC.setOnClickListener {
                this@WorkFragment.viewModel.onNavigateToAddNewSparePartsList()
                findNavController().navigate(
                    OrderDetailsFragmentDirections.actionOrderDetailsFragmentToAddSparePartsListFragment(
                        false
                    )
                )
            }

            addZCDiagnose.setOnClickListener {
                this@WorkFragment.viewModel.onNavigateToAddNewSparePartsList()
                findNavController().navigate(
                    OrderDetailsFragmentDirections.actionOrderDetailsFragmentToAddSparePartsListFragment(
                        true
                    )
                )
            }

            val adapter = EngineersSelectListAdapter(EngineersSelectListAdapter.OnClickListener {})
            engineersRecyclerView.adapter = adapter
            this@WorkFragment.viewModel.order.observe(viewLifecycleOwner) {
                adapter.submitList(it?.engineersItems?.map { item -> item.engineer })
            }
        }
    }

    private fun setEnabled(isMainUser: Boolean?) {
        isMainUser?.let {
            binding.apply {
                startWorkBtn.isEnabled = isMainUser
                endWorkBtn.isEnabled = isMainUser
                addZC.isEnabled = isMainUser
                addZCDiagnose.isEnabled = isMainUser
                clientIssueDefinition.isEnabled = isMainUser
                workDefinition.isEnabled = isMainUser
                quickReport.isEnabled = isMainUser
                createGuaranteeOrder.isEnabled = isMainUser
                workNotGuaranteed.isEnabled = isMainUser
                addEngineer.isEnabled = isMainUser
                addUsedPart.isEnabled = isMainUser
                usedSparePartsRecyclerView.isEnabled = isMainUser
            }
        }
    }

    private fun setWorksEnded() {
        setEnabled(false)
        binding.servicesRecyclerView.isEnabled = false

        for (i in 0 until binding.servicesRecyclerView.childCount) {
            val holder: ServicesListAdapter.VH =
                binding.servicesRecyclerView.findViewHolderForAdapterPosition(i) as ServicesListAdapter.VH
            holder.disableFields()
        }
    }


    private fun setAutoDriveTime(isEnd: Boolean = false) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Укажите моточасы")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)

        builder.setPositiveButton("ОК") { _, _ ->
            viewModel.setAutoDriveTime(input.text.toString().toDoubleOrNull(), isEnd)
        }
        builder.setNegativeButton(
            "Отменить"
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}