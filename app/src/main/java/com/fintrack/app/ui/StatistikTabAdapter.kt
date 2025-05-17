package com.fintrack.app.ui
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

    fun update(newCalendar: Calendar, newMode: String) {
        calendar = newCalendar
        mode = newMode
        notifyDataSetChanged()
    }
}