package com.fintrack.app.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.fintrack.app.R

class TambahAnggaranBaruFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_anggaran_baru, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnKembali = view.findViewById<ImageView>(R.id.button_kembali_tambah_anggaran)
        btnKembali.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val buttonSimpan = view.findViewById<Button>(R.id.button_simpan_anggaran)
        buttonSimpan.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Berhasil")
                .setMessage("Anggaran berhasil disimpan.")
                .setPositiveButton("Oke") { dialog, _ ->
                    dialog.dismiss()
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .setCancelable(false)
                .show()
        }
    }
}
