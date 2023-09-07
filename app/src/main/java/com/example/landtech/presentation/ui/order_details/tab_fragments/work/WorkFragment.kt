package com.example.landtech.presentation.ui.order_details.tab_fragments.work

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.landtech.databinding.FragmentWorkBinding
import com.example.landtech.presentation.ui.order_details.OrderDetailsFragmentDirections
import com.example.landtech.presentation.ui.order_details.OrderDetailsViewModel
import java.util.Locale
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

            addEngineer.setOnClickListener {
                findNavController().navigate(OrderDetailsFragmentDirections.actionOrderDetailsFragmentToEngineersSelectFragment())
            }

            voiceDefinitionBtn.setOnClickListener {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale("ru", "RU")
                )

                startSpeechToText.launch(intent)
            }
        }
    }
}