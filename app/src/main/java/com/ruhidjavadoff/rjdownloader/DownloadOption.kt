package com.ruhidjavadoff.rjdownloader

import android.os.Parcelable
import kotlinx.parcelize.Parcelize // Parcelize importu UNUTMA

@Parcelize
data class DownloadOption(
    val format: String,
    val quality: String,
    val directUrl: String
) : Parcelable // Parcelable interfeysi