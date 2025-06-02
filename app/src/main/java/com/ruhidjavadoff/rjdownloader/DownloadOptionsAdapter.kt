package com.ruhidjavadoff.rjdownloader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DownloadOptionsAdapter(
    private val options: List<DownloadOption>,
    private val onDownloadClick: (DownloadOption) -> Unit
) : RecyclerView.Adapter<DownloadOptionsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFormat: TextView = view.findViewById(R.id.tvFormat)
        val tvQuality: TextView = view.findViewById(R.id.tvQuality)
        val btnDownload: ImageButton = view.findViewById(R.id.btnDownloadOption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_download_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.tvFormat.text = option.format
        holder.tvQuality.text = option.quality
        holder.btnDownload.setOnClickListener {
            onDownloadClick(option)
        }
    }

    override fun getItemCount() = options.size
}