package com.fintrack.app.utils

import com.fintrack.app.R
import java.util.Locale

object CategoryIconMapper {
    fun getIconForCategory(categoryName: String?): Int {
        return when (categoryName?.lowercase(Locale.getDefault())) {
            "makanan & minuman" -> R.drawable.ic_restaurant
            "transportasi" -> R.drawable.ic_transport
            "belanja" -> R.drawable.ic_shopping_cart
            "tagihan" -> R.drawable.ic_receipt
            "hiburan" -> R.drawable.ic_entertainment
            "kesehatan" -> R.drawable.ic_health
            "pendidikan" -> R.drawable.ic_education
            "keluarga" -> R.drawable.ic_family
            "perawatan diri" -> R.drawable.ic_personal_care

            // Income Categories
            "gaji" -> R.drawable.ic_salary
            "bonus" -> R.drawable.ic_bonus
            "investasi" -> R.drawable.ic_investment

            // Default
            else -> R.drawable.ic_other_category // Ikon default jika kategori tidak ditemukan
        }
    }
}
