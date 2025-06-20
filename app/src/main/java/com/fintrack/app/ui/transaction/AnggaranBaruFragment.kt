package com.fintrack.app.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fintrack.app.R // Pastikan ini mengarah ke R class Anda

class AnggaranBaruFragment : Fragment() { // Pastikan nama kelasnya AnggaranBaruFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Pastikan layout ini ada: fragment_anggaran_baru.xml
        return inflater.inflate(R.layout.fragment_tambah_anggaran, container, false)
    }
}