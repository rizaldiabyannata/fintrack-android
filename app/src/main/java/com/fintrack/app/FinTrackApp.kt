package com.fintrack.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Kelas Application dasar yang diperlukan oleh Hilt.
 * Anotasi @HiltAndroidApp akan memicu Hilt untuk men-generate kode
 * yang diperlukan untuk dependency injection di seluruh aplikasi.
 *
 * Kelas ini harus didaftarkan di dalam AndroidManifest.xml
 */
@HiltAndroidApp
class FinTrackApp : Application() {
    // Untuk setup dasar Hilt, isi kelas ini bisa dibiarkan kosong.
}
