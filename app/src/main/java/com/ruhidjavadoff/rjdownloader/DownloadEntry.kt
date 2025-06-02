package com.ruhidjavadoff.rjdownloader

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadEntry(
    val id: Long,
    val fileName: String?,
    val uri: String?,
    val status: Int,
    val reason: Int,
    val bytesDownloaded: Long,
    val totalSizeBytes: Long,
    var localUri: String? = null,
    var mimeType: String? = null // YENİ HISSE
) : Parcelable {
    val progress: Int
        get() = if (totalSizeBytes <= 0) 0 else ((bytesDownloaded * 100) / totalSizeBytes).toInt()
}