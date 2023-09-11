package com.example.landtech.presentation.ui.order_details.tab_fragments.work

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.landtech.databinding.FragmentWorkBinding
import com.example.landtech.presentation.ui.order_details.OrderDetailsFragmentDirections
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel
import java.util.Objects


class WorkFragment : Fragment() {
    private lateinit var binding: FragmentWorkBinding
    private val viewModel: OrderDetailsViewModel by activityViewModels()

    private val startSpeechToText =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val textArr = data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )
                    binding.quickReport.setText(Objects.requireNonNull(textArr)?.get(0))
                }

                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

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
            servicesRecyclerView.adapter = ServicesListAdapter()
            usedSparePartsRecyclerView.adapter = UsedPartsListAdapter()

            addEngineer.setOnClickListener {
                findNavController().navigate(OrderDetailsFragmentDirections.actionOrderDetailsFragmentToEngineersSelectFragment())
            }

            startWorkBtn.setOnClickListener {
                setAutoDriveTime()
            }

            endWorkBtn.setOnClickListener {
                setAutoDriveTime()
            }

            voiceDefinitionBtn.setOnClickListener {
                val language = "ru-RU"

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL, language
                )
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE, language
                )

                startSpeechToText.launch(intent)
            }
        }
    }

    private fun setAutoDriveTime() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Укажите моточасы")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)

        builder.setPositiveButton("ОК") { _, _ ->
            viewModel.setAutoDriveTime(input.text.toString().toDoubleOrNull())
        }
        builder.setNegativeButton(
            "Отменить"
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}