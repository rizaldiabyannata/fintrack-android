package com.fintrack.app.ui.addBudget

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.databinding.FragmentBudgetBinding

class AddBudgetFragment : AppCompatActivity() {
    private lateinit var binding: FragmentBudgetBinding

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        binding = FragmentBudgetBinding.inflate(layoutInflater)
        val view = binding.root
        return super.onCreateView(name, context, attrs)
    }
}