package com.example.skinfriend.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun <T : RecyclerView.ViewHolder> setupRecyclerView(
    recyclerView: RecyclerView,
    adapter: RecyclerView.Adapter<T>,
    context: Context
) {
    recyclerView.layoutManager = LinearLayoutManager(context)
    recyclerView.adapter = adapter
}

fun getDate(): String {
    val dateFormat = SimpleDateFormat("EEE, dd/MM/yy", Locale.getDefault())
    val date = Date()
    return dateFormat.format(date)
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

