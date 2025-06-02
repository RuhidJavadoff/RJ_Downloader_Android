package com.ruhidjavadoff.rjdownloader

import android.app.DownloadManager
import android.view.LayoutInflater
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class DownloadsAdapter(
    private val onActionClick: (downloadEntry: DownloadEntry, action: String) -> Unit
) : ListAdapter<DownloadEntry, DownloadsAdapter.ViewHolder>(DownloadEntryDiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgThumbnail: ImageView = view.findViewById(R.id.imgDownloadItemThumbnail)
        val tvFileName: TextView = view.findViewById(R.id.tvDownloadItemFileName)
        val pbProgress: ProgressBar = view.findViewById(R.id.pbDownloadItemProgress)
        val tvStatus: TextView = view.findViewById(R.id.tvDownloadItemStatus)
        val btnAction: ImageButton = view.findViewById(R.id.btnDownloadItemPauseResume)
        val btnCancel: ImageButton = view.findViewById(R.id.btnDownloadItemCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_download_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvFileName.text = item.fileName ?: "Unknown File"
        holder.pbProgress.progress = item.progress
        holder.btnCancel.visibility = View.VISIBLE

        val context = holder.itemView.context
        val downloadedSizeFormatted = formatBytes(item.bytesDownloaded)
        val totalSizeFormatted = formatBytes(item.totalSizeBytes)

        when (item.status) {
            DownloadManager.STATUS_PENDING -> {
                holder.tvStatus.text = context.getString(R.string.downloads_status_pending)
                holder.pbProgress.visibility = View.VISIBLE
                holder.pbProgress.isIndeterminate = true
                holder.btnAction.visibility = View.GONE
                holder.btnCancel.setOnClickListener { onActionClick(item, "CANCEL") }
            }
            DownloadManager.STATUS_RUNNING -> {
                holder.tvStatus.text = context.getString(R.string.downloads_status_downloading_detailed, item.progress, downloadedSizeFormatted, totalSizeFormatted)
                holder.pbProgress.visibility = View.VISIBLE
                holder.pbProgress.isIndeterminate = false
                holder.btnAction.visibility = View.GONE
                holder.btnCancel.setOnClickListener { onActionClick(item, "CANCEL") }
            }
            DownloadManager.STATUS_PAUSED -> {
                holder.tvStatus.text = context.getString(R.string.downloads_status_paused_detailed, item.progress, downloadedSizeFormatted, totalSizeFormatted)
                holder.pbProgress.visibility = View.VISIBLE
                holder.pbProgress.isIndeterminate = false
                holder.btnAction.visibility = View.GONE // Və ya "Davam Et" üçün fərqli məntiq
                holder.btnCancel.setOnClickListener { onActionClick(item, "CANCEL") }
            }
            DownloadManager.STATUS_SUCCESSFUL -> {
                holder.tvStatus.text = context.getString(R.string.downloads_status_completed)
                holder.pbProgress.visibility = View.GONE
                holder.btnAction.setImageResource(R.drawable.ic_open_file)
                holder.btnAction.contentDescription = "Faylı aç"
                holder.btnAction.visibility = View.VISIBLE
                holder.btnAction.setOnClickListener { onActionClick(item, "OPEN") }
                holder.btnCancel.setImageResource(R.drawable.ic_cancel_download) // Sile
                holder.btnCancel.contentDescription = "Yükləməni sil"
                holder.btnCancel.setOnClickListener { onActionClick(item, "DELETE_COMPLETED") }
            }
            DownloadManager.STATUS_FAILED -> {
                holder.tvStatus.text = context.getString(R.string.downloads_status_failed) + " (Səbəb: ${getReasonText(item.reason, context)})"
                holder.pbProgress.visibility = View.GONE
                holder.btnAction.setImageResource(R.drawable.ic_retry_download)
                holder.btnAction.contentDescription = "Yenidən cəhd et"
                holder.btnAction.visibility = View.VISIBLE
                holder.btnAction.setOnClickListener { onActionClick(item, "RETRY") }
                holder.btnCancel.setImageResource(R.drawable.ic_cancel_download) // Sile ucun
                holder.btnCancel.contentDescription = "Xətalı yükləməni sil"
                holder.btnCancel.setOnClickListener { onActionClick(item, "DELETE_FAILED") }
            }
            else -> {
                holder.tvStatus.text = "Naməlum status: ${item.status}"
                holder.btnAction.visibility = View.GONE
                holder.btnCancel.visibility = View.GONE
            }
        }

        // Thumbnail hissesi
        setThumbnail(holder.imgThumbnail, item)
    }

    private fun setThumbnail(imageView: ImageView, item: DownloadEntry) {
        // Hələlik Glide/Coil istifadə etmemisem
        val mimeType = item.mimeType
        when {
            mimeType?.startsWith("video/") == true -> {
                imageView.setImageResource(R.drawable.ic_file_video)
            }
            mimeType?.startsWith("audio/") == true -> {
                imageView.setImageResource(R.drawable.ic_file_audio)
            }
            mimeType?.startsWith("image/") == true -> {
                imageView.setImageResource(R.drawable.ic_file_image)
            }
            else -> {
                imageView.setImageResource(R.drawable.ic_file_generic) // Ümumi fayl ucun (deyisile biler)
            }
        }
        imageView.setBackgroundColor(0) // arxa fon delete
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes < 0) return "0 B"
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb)
        val mb = kb / 1024.0
        if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb)
        val gb = mb / 1024.0
        return String.format(Locale.US, "%.1f GB", gb)
    }

    private fun getReasonText(reasonCode: Int, context: Context): String {
        return when (reasonCode) {
            DownloadManager.ERROR_CANNOT_RESUME -> "Davam etdirilə bilmir"
            DownloadManager.ERROR_DEVICE_NOT_FOUND -> "Cihaz tapılmadı"
            DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "Fayl artıq mövcuddur"
            DownloadManager.ERROR_FILE_ERROR -> "Fayl xətası"
            DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP məlumat xətası"
            DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Kifayət qədər yer yoxdur"
            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Çox sayda yönləndirmə"
            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "İdarə olunmayan HTTP kodu"
            DownloadManager.ERROR_UNKNOWN -> "Naməlum xəta"
            else -> "Xəta kodu: $reasonCode"
        }
    }
}

class DownloadEntryDiffCallback : DiffUtil.ItemCallback<DownloadEntry>() {
    override fun areItemsTheSame(oldItem: DownloadEntry, newItem: DownloadEntry): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: DownloadEntry, newItem: DownloadEntry): Boolean {
        return oldItem == newItem
    }
}