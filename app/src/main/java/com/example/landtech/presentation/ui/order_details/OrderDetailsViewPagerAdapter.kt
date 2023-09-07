package com.example.landtech.presentation.ui.order_details

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.landtech.presentation.ui.order_details.tab_fragments.machinery.MachineryFragment
import com.example.landtech.presentation.ui.order_details.tab_fragments.spare_parts.SparePartsFragment
import com.example.landtech.presentation.ui.order_details.tab_fragments.work.WorkFragment

class OrderDetailsViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MachineryFragment()
            1 -> WorkFragment()
            2 -> SparePartsFragment()
            else -> MachineryFragment()
        }
    }
}