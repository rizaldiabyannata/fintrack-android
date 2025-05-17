package com.fintrack.app.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fintrack.app.ui.transaksi.TabBulananFragment
import com.fintrack.app.ui.transaksi.TabHarianFragment
import com.fintrack.app.ui.transaksi.TabTutupBukuFragment

class TransaksiTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = listOf(
        TabHarianFragment(),
        TabBulananFragment(),
        TabTutupBukuFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}

