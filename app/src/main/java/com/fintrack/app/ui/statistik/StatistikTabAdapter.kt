package com.fintrack.app.ui.statistik

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.Calendar

class StatistikTabAdapter(fragment: Fragment, private var calendar: Calendar, private var mode: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val type = when (position) {
            0 -> "keseluruhan"
            1 -> "pendapatan"
            2 -> "pengeluaran"
            else -> "keseluruhan"
        }

        return StatistikTabFragment.newInstance(type, calendar.time, mode)
    }

    // supaya fragment dibuat ulang saat update
    override fun getItemId(position: Int): Long {
        // Gabungkan posisi, tanggal dan mode sebagai ID unik
        return (position + calendar.timeInMillis + mode.hashCode()).hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return false
    }

    fun update(newCalendar: Calendar, newMode: String) {
        calendar = newCalendar
        mode = newMode
        notifyDataSetChanged()
    }
}
