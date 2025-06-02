package com.ruhidjavadoff.rjdownloader

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class DownloadsFragment : Fragment() {

    private lateinit var rvDownloadsList: RecyclerView
    private lateinit var downloadsAdapter: DownloadsAdapter
    private lateinit var downloadManager: DownloadManager

    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable: Runnable = object : Runnable {
        override fun run() {
            loadDownloads()
            handler.postDelayed(this, REFRESH_INTERVAL_MS)
        }
    }

    companion object {
        private const val REFRESH_INTERVAL_MS = 1500L
        private const val SAD_MUSIQILAR_PACKAGE_NAME = "com.ruhidjavadoff.sadmusiqialar"
        private const val COLUMN_MIME_TYPE_STRING = "media_type"
    }

    private val onDownloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId != -1L) {
                    Log.d("DownloadsFragment", "Yükləmə tamamlandı: ID $downloadId")
                    loadDownloads()
                }
            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED == action) {
                Toast.makeText(context, "Yükləmə bildirişinə klikləndi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_downloads, container, false)
        rvDownloadsList = view.findViewById(R.id.rvDownloadsList)
        setupRecyclerView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33
            requireContext().registerReceiver(onDownloadCompleteReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            requireContext().registerReceiver(onDownloadCompleteReceiver, intentFilter)
        }
    }

    private fun setupRecyclerView() {
        rvDownloadsList.layoutManager = LinearLayoutManager(requireContext())
        downloadsAdapter = DownloadsAdapter { downloadEntry, action ->
            handleDownloadAction(downloadEntry, action)
        }
        rvDownloadsList.adapter = downloadsAdapter
    }

    private fun loadDownloads() {
        val downloads = mutableListOf<DownloadEntry>()
        val query = DownloadManager.Query()
        var cursor: Cursor? = null
        try {
            cursor = downloadManager.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                val idCol = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
                val titleCol = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
                val uriCol = cursor.getColumnIndex(DownloadManager.COLUMN_URI)
                val statusCol = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val reasonCol = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                val bytesDCol = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val bytesTCol = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                val localUriCol = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                // DÜZƏLİŞ: Sabit yerinə string dəyərini istifadə edirik
                val mimeTypeCol = cursor.getColumnIndex(COLUMN_MIME_TYPE_STRING)

                if (idCol == -1 || titleCol == -1 || uriCol == -1 || statusCol == -1 || reasonCol == -1 ||
                    bytesDCol == -1 || bytesTCol == -1 || localUriCol == -1 || mimeTypeCol == -1) {
                    Log.e("DownloadsFragment", "DownloadManager sütunlarından biri tapılmadı. Sütun adlarını yoxlayın. MimeTypeCol index: $mimeTypeCol")
                    return
                }

                do {
                    val id = cursor.getLong(idCol)
                    var title = cursor.getString(titleCol)
                    val uri = cursor.getString(uriCol)
                    val status = cursor.getInt(statusCol)
                    val reason = cursor.getInt(reasonCol)
                    val bytesDownloaded = cursor.getLong(bytesDCol)
                    val totalSizeBytes = cursor.getLong(bytesTCol)
                    val localUriFromCursor = cursor.getString(localUriCol)
                    val mimeType = cursor.getString(mimeTypeCol)

                    if (title.isNullOrBlank() && uri != null) {
                        title = Uri.parse(uri).lastPathSegment ?: "fayl_${id}"
                    }
                    if (title.isNullOrBlank() && localUriFromCursor != null) {
                        try {
                            val localFileUri = Uri.parse(localUriFromCursor)
                            if (localFileUri.scheme == "content") {
                                requireContext().contentResolver.query(localFileUri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { nameCursor ->
                                    if (nameCursor.moveToFirst()) {
                                        val nameIndex = nameCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                        if (nameIndex != -1) title = nameCursor.getString(nameIndex)
                                    }
                                }
                            } else if (localFileUri.scheme == "file") {
                                title = File(localFileUri.path!!).name
                            }
                        } catch (e: Exception) {
                            Log.w("DownloadsFragment", "Lokal URI-dən ad alına bilmədi: $localUriFromCursor", e)
                        }
                    }
                    if (title.isNullOrBlank()){
                        title = "Fayl ID $id"
                    }

                    downloads.add(
                        DownloadEntry(
                            id, title, uri, status, reason,
                            bytesDownloaded, totalSizeBytes, localUriFromCursor, mimeType
                        )
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DownloadsFragment", "Yükləmələri oxuyarkən xəta", e)
        } finally {
            cursor?.close()
        }
        downloadsAdapter.submitList(downloads.sortedByDescending { it.id })
    }

    private fun handleDownloadAction(downloadEntry: DownloadEntry, action: String) {
        when (action) {
            "CANCEL" -> {
                if (downloadEntry.status == DownloadManager.STATUS_RUNNING ||
                    downloadEntry.status == DownloadManager.STATUS_PENDING ||
                    downloadEntry.status == DownloadManager.STATUS_PAUSED) {
                    val removedCount = downloadManager.remove(downloadEntry.id)
                    if (removedCount > 0) {
                        Toast.makeText(context, "${downloadEntry.fileName ?: "Fayl"} ləğv edildi", Toast.LENGTH_SHORT).show()
                        loadDownloads()
                    } else {
                        Toast.makeText(context, "Ləğv etmək mümkün olmadı", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Bu yükləmə artıq ləğv edilə bilməz", Toast.LENGTH_SHORT).show()
                }
            }
            "OPEN" -> {
                if (downloadEntry.status != DownloadManager.STATUS_SUCCESSFUL || downloadEntry.localUri == null) {
                    Toast.makeText(context, "Fayl hələ yüklənməyib və ya tapılmadı", Toast.LENGTH_SHORT).show()
                    return
                }
                val localFileUri = Uri.parse(downloadEntry.localUri)
                val mimeTypeToUse = downloadManager.getMimeTypeForDownloadedFile(downloadEntry.id) ?: downloadEntry.mimeType

                Log.d("DownloadsFragment", "Fayl açılır: $localFileUri, MimeType: $mimeTypeToUse")

                if (mimeTypeToUse != null && mimeTypeToUse.startsWith("video/")) {
                    openFileWithIntent(localFileUri, mimeTypeToUse)
                } else {
                    if (isAppInstalled(SAD_MUSIQILAR_PACKAGE_NAME, requireContext())) {
                        val launchIntent = requireContext().packageManager.getLaunchIntentForPackage(SAD_MUSIQILAR_PACKAGE_NAME)
                        if (launchIntent != null) {
                            launchIntent.action = Intent.ACTION_VIEW
                            val actualFileUriToShare: Uri
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && "file" == localFileUri.scheme) {
                                val file = File(localFileUri.path!!)
                                actualFileUriToShare = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
                            } else {
                                actualFileUriToShare = localFileUri
                            }
                            launchIntent.setDataAndType(actualFileUriToShare, mimeTypeToUse ?: "*/*")
                            launchIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                            if (launchIntent.resolveActivity(requireActivity().packageManager) != null) {
                                startActivity(launchIntent)
                            } else {
                                Log.w("DownloadsFragment", "$SAD_MUSIQILAR_PACKAGE_NAME bu faylı aça bilmədi, ümumi chooser göstərilir.")
                                openFileWithIntent(localFileUri, mimeTypeToUse)
                            }
                        } else {
                            Toast.makeText(context, "$SAD_MUSIQILAR_PACKAGE_NAME açıla bilmədi.", Toast.LENGTH_LONG).show()
                            openPlayStoreForApp(SAD_MUSIQILAR_PACKAGE_NAME)
                        }
                    } else {
                        openPlayStoreForApp(SAD_MUSIQILAR_PACKAGE_NAME)
                    }
                }
            }
            "RETRY" -> {
                downloadManager.remove(downloadEntry.id)
                if (downloadEntry.uri != null) {
                    val request = DownloadManager.Request(Uri.parse(downloadEntry.uri))
                        .setTitle(downloadEntry.fileName ?: Uri.parse(downloadEntry.uri).lastPathSegment ?: "download_retry")
                        .setDescription("Yenidən cəhd edilir...")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadEntry.fileName ?: "retry_${System.currentTimeMillis()}")
                        .setMimeType(downloadEntry.mimeType ?: "*/*")
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)
                    try {
                        downloadManager.enqueue(request)
                        Toast.makeText(context, "${downloadEntry.fileName ?: "Fayl"} üçün yenidən cəhd edilir", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Yenidən cəhd zamanı xəta: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Yenidən cəhd üçün orijinal URL tapılmadı", Toast.LENGTH_SHORT).show()
                }
                loadDownloads()
            }
            "DELETE_COMPLETED", "DELETE_FAILED" -> {
                val removedCount = downloadManager.remove(downloadEntry.id)
                if (removedCount > 0) {
                    Toast.makeText(context, "${downloadEntry.fileName ?: "Fayl"} silindi", Toast.LENGTH_SHORT).show()
                    loadDownloads()
                } else {
                    Toast.makeText(context, "Silmək mümkün olmadı (bəlkə artıq silinib)", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openFileWithIntent(fileUri: Uri, mimeType: String?) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val actualFileUri: Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && "file" == fileUri.scheme) {
                val file = File(fileUri.path!!)
                actualFileUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
            } else {
                actualFileUri = fileUri
            }
            intent.setDataAndType(actualFileUri, mimeType ?: "*/*")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(Intent.createChooser(intent, "Faylı aç..."))
            } else {
                Toast.makeText(context, "Bu fayl növünü ($mimeType) aça biləcək proqram tapılmadı", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("DownloadsFragment", "Fayl açma xətası", e)
            Toast.makeText(context, "Faylı açmaq mümkün olmadı: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isAppInstalled(packageName: String, context: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName, 0)
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun openPlayStoreForApp(packageName: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            requireContext().unregisterReceiver(onDownloadCompleteReceiver)
        } catch (e: IllegalArgumentException) {
            Log.w("DownloadsFragment", "Receiver artıq qeydiyyatdan silinmişdi və ya heç vaxt qeydiyyatdan keçməmişdi.", e)
        }
    }
}
