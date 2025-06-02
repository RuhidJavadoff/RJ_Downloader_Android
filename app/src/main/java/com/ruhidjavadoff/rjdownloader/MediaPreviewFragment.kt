package com.ruhidjavadoff.rjdownloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MediaPreviewFragment : Fragment() {

    private lateinit var imgMediaThumbnail: ImageView
    private lateinit var tvMediaTitle: TextView
    private lateinit var rvMediaDownloadOptions: RecyclerView
    private lateinit var downloadOptionsAdapter: DownloadOptionsAdapter

    private var mediaTitle: String? = null
    private var mediaThumbnailUrl: String? = null
    private var downloadOptions: ArrayList<DownloadOption>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Argumentləri burada alırıq
        arguments?.let {
            mediaTitle = it.getString("media_title")
            mediaThumbnailUrl = it.getString("media_thumbnail_url")
            // DÜZƏLİŞ: ParcelableArrayList olaraq alırıq
            downloadOptions = it.getParcelableArrayList("download_options")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_media_preview, container, false)

        imgMediaThumbnail = view.findViewById(R.id.imgMediaThumbnail)
        tvMediaTitle = view.findViewById(R.id.tvMediaTitle)
        rvMediaDownloadOptions = view.findViewById(R.id.rvMediaDownloadOptions)


        tvMediaTitle.text = mediaTitle ?: getString(R.string.media_preview_title_placeholder)


        if (mediaThumbnailUrl != null) {
            Log.d("MediaPreviewFragment", "Thumbnail URL: $mediaThumbnailUrl")

            imgMediaThumbnail.setBackgroundColor(requireContext().getColor(android.R.color.darker_gray)) // Sadə placeholder
        }

        setupRecyclerView()

        return view
    }

    private fun setupRecyclerView() {
        rvMediaDownloadOptions.layoutManager = LinearLayoutManager(requireContext())
        downloadOptionsAdapter = DownloadOptionsAdapter(downloadOptions ?: emptyList()) { selectedOption ->
            startDownload(selectedOption)
        }
        rvMediaDownloadOptions.adapter = downloadOptionsAdapter
    }


    private fun startDownload(option: DownloadOption) {
        try {
            val titleForFile = mediaTitle?.replace(Regex("[^a-zA-Z0-9.-]"), "_")?.take(50) ?: "download"
            val qualityForFile = option.quality.replace(Regex("[^a-zA-Z0-9.-]"), "_").take(30)
            var extension = "dat"
            val formatPart = option.quality.split(" - ").getOrNull(1)?.lowercase() ?: option.format.lowercase()

            if (option.format.contains("Audio", ignoreCase = true)) {
                extension = when {
                    formatPart.contains("m4a") -> "m4a"
                    formatPart.contains("mp3") -> "mp3"
                    formatPart.contains("opus") -> "opus"
                    formatPart.contains("ogg") -> "ogg"
                    else -> "mp3"
                }
            } else if (option.format.contains("Video", ignoreCase = true)) {
                extension = when {
                    formatPart.contains("mp4") -> "mp4"
                    formatPart.contains("webm") -> "webm"
                    else -> "mp4"
                }
            }

            val fileName = "RJ-${titleForFile}-${qualityForFile}.$extension"

            val request = DownloadManager.Request(Uri.parse(option.directUrl))
                .setTitle(fileName)
                .setDescription("${option.format} - ${option.quality}")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setMimeType(option.mimeType(extension))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            val toastMessage = getString(R.string.message_download_started, fileName)
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("MediaPreviewFragment", "Yükləmə xətası", e)
            val errorMessage = getString(R.string.message_download_error, e.message)
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun DownloadOption.mimeType(actualExtension: String): String {
        return when (actualExtension) {
            "mp3" -> "audio/mpeg"; "m4a" -> "audio/mp4"; "opus" -> "audio/opus"; "ogg" -> "audio/ogg"
            "mp4" -> "video/mp4"; "webm" -> "video/webm"
            else -> if (this.format.contains("Audio", ignoreCase = true)) "audio/*" else if (this.format.contains("Video", ignoreCase = true)) "video/*" else "*/*"
        }
    }
}