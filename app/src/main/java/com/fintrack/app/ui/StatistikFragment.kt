import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.fintrack.app.R
import com.fintrack.app.ui.StatistikTabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.PopupMenu


class StatistikFragment : Fragment() {
    private lateinit var txtMonthYear: TextView
    private lateinit var btnPrevMonth: ImageButton
    private lateinit var btnNextMonth: ImageButton
    private lateinit var txtWaktu: TextView
    private lateinit var btnDropdown: ImageButton

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private var calendar = Calendar.getInstance()
    private var mode: String = "Bulanan"

    private lateinit var adapter: StatistikTabAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistik, container, false)

        txtMonthYear = view.findViewById(R.id.txt_month_year)
        btnPrevMonth = view.findViewById(R.id.btn_prev_month)
        btnNextMonth = view.findViewById(R.id.btn_next_month)
        txtWaktu = view.findViewById(R.id.txt_waktu)
        btnDropdown = view.findViewById(R.id.btn_dropdown)
        tabLayout = view.findViewById(R.id.tabStatistik)
        viewPager = view.findViewById(R.id.viewPagerStatistik)

        adapter = StatistikTabAdapter(this, calendar, mode)
        viewPager.adapter = adapter

        val tabTitles = listOf("Keseluruhan", "Pendapatan", "Pengeluaran")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        updateMonthYearText()

        btnPrevMonth.setOnClickListener {
            if (mode == "Bulanan") {
                calendar.add(Calendar.MONTH, -1)
            } else {
                calendar.add(Calendar.YEAR, -1)
            }
            updateMonthYearText()
            refreshAdapter()
        }

        btnNextMonth.setOnClickListener {
            if (mode == "Bulanan") {
                calendar.add(Calendar.MONTH, 1)
            } else {
                calendar.add(Calendar.YEAR, 1)
            }
            updateMonthYearText()
            refreshAdapter()
        }

        btnDropdown.setOnClickListener {
            val popup = PopupMenu(requireContext(), btnDropdown)
            popup.menuInflater.inflate(R.menu.menu_waktu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                // logic statik bulanan atau tahunan
                when (item.itemId) {
                    R.id.menu_bulanan -> {
                        mode = "Bulanan"
                        txtWaktu.text = "Bulanan"
                        updateMonthYearText()
                        refreshAdapter()
                        true
                    }
                    R.id.menu_tahunan -> {
                        mode = "Tahunan"
                        txtWaktu.text = "Tahunan"
                        updateMonthYearText()
                        refreshAdapter()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        return view
    }

    private fun updateMonthYearText() {
        val format = if (mode == "Bulanan") {
            SimpleDateFormat("MMM yyyy", Locale.getDefault())
        } else {
            SimpleDateFormat("yyyy", Locale.getDefault())
        }
        txtMonthYear.text = format.format(calendar.time)
    }

    private fun refreshAdapter() {
        adapter.update(calendar, mode)
    }

}
