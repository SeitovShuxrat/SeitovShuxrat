package com.example.landtech.presentation.ui.order_details.tab_fragments.spare_parts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.landtech.databinding.FragmentSparePartsBinding

class SparePartsFragment : Fragment() {
    private lateinit var binding: FragmentSparePartsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSparePartsBinding.inflate(inflater, container, false)
        return binding.root
    }
}