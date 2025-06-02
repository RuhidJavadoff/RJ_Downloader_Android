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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.localization.Localization
import org.schabi.newpipe.extractor.stream.StreamInfo

class SearchDetailFragment : Fragment() {


    interface OnSearchDetailInteractionListener {
        fun onMediaReadyForPreview(
            title: String?,
            thumbnailUrl: String?,
            downloadOptions: List<DownloadOption>
        )
    }

    private var searchDetailListener: OnSearchDetailInteractionListener? = null

    private lateinit var etSearchDetail: EditText
    private lateinit var btnSearch: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvDownloadOptionsTitle: TextView
    private lateinit var rvDownloadOptions: RecyclerView
    private lateinit var downloadOptionsAdapter: DownloadOptionsAdapter

    private var newPipeInitialized = false

    // Sayt ikonları
    private lateinit var iconYoutube: ImageView
    private lateinit var iconInstagram: ImageView
    private lateinit var iconFacebook: ImageView
    private lateinit var iconTiktok: ImageView
    private lateinit var iconWhatsapp: ImageView
    private lateinit var iconX: ImageView


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSearchDetailInteractionListener) {
            searchDetailListener = context
        } else {
            throw RuntimeException("$context must implement OnSearchDetailInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_detail, container, false)

        etSearchDetail = view.findViewById(R.id.etSearchDetail)
        btnSearch = view.findViewById(R.id.btnSearchDetail)
        progressBar = view.findViewById(R.id.progressBar)
        tvDownloadOptionsTitle = view.findViewById(R.id.tvDownloadOptionsTitle)
        rvDownloadOptions = view.findViewById(R.id.rvRecentSearches)

        iconYoutube = view.findViewById(R.id.iconYoutube)
        iconInstagram = view.findViewById(R.id.iconInstagram)
        iconFacebook = view.findViewById(R.id.iconFacebook)
        iconTiktok = view.findViewById(R.id.iconTiktok)
        iconWhatsapp = view.findViewById(R.id.iconWhatsapp)
        iconX = view.findViewById(R.id.iconX)

        setupDummyRecyclerView()
        setupClickListeners()

        logRecentSearches()

        return view
    }

    private fun setupDummyRecyclerView() {
        rvDownloadOptions.layoutManager = LinearLayoutManager(requireContext())
        downloadOptionsAdapter = DownloadOptionsAdapter(emptyList()) {

        }
        rvDownloadOptions.adapter = downloadOptionsAdapter
        tvDownloadOptionsTitle.visibility = View.GONE
        rvDownloadOptions.visibility = View.GONE
    }

    private fun setupClickListeners() {
        btnSearch.setOnClickListener {
            val url = etSearchDetail.text.toString().trim()
            if (url.isNotEmpty()) {
                SearchHistoryManager.addSearchQuery(requireContext(), url)
                logRecentSearches()

                lifecycleScope.launch {
                    findDownloadLinksAsync(url)
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.message_please_enter_link), Toast.LENGTH_SHORT).show()
            }
        }

        iconYoutube.setOnClickListener { setSiteUrl("Nümunə YouTube linki...") }
        iconInstagram.setOnClickListener { setSiteUrl("Nümunə Instagram linki...") }
        iconFacebook.setOnClickListener { setSiteUrl("Nümunə Facebook linki...") }
        iconTiktok.setOnClickListener { setSiteUrl("Nümunə TikTok linki...") }
        iconWhatsapp.setOnClickListener { setSiteUrl("Nümunə WhatsApp status linki...") }
        iconX.setOnClickListener { setSiteUrl("Nümunə X (Twitter) linki...") }
    }

    private fun setSiteUrl(sampleUrlOrHint: String) {
        etSearchDetail.setText(sampleUrlOrHint)
        etSearchDetail.requestFocus()
    }

    private fun logRecentSearches() {
        val recentSearches = SearchHistoryManager.getRecentSearches(requireContext())
        Log.d("SearchDetailFragment", "Son Axtarışlar: $recentSearches")
    }

    private suspend fun findDownloadLinksAsync(url: String) {
        withContext(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE
            tvDownloadOptionsTitle.visibility = View.GONE
            rvDownloadOptions.visibility = View.GONE
            Toast.makeText(requireContext(), "'$url' üçün seçimlər axtarılır...", Toast.LENGTH_SHORT).show()
        }

        val result: Result<Pair<StreamInfo?, List<DownloadOption>>> = parseUrlAndGetOptionsReturnStreamInfo(url)

        withContext(Dispatchers.Main) {
            progressBar.visibility = View.GONE
            result.onSuccess { (streamInfo, options) ->
                if (options.isNotEmpty() && streamInfo != null) {
                    // DÜZƏLİŞ: Thumbnail URL üçün null ötürülür
                    searchDetailListener?.onMediaReadyForPreview(streamInfo.name, null, options)
                } else {
                    Toast.makeText(requireContext(), "Bu link üçün yükləmə seçimi tapılmadı.", Toast.LENGTH_LONG).show()
                }
            }.onFailure { exception ->
                Log.e("SearchDetailFragment", "Link analiz xətası", exception)
                Toast.makeText(requireContext(), "Xəta: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun parseUrlAndGetOptionsReturnStreamInfo(url: String): Result<Pair<StreamInfo?, List<DownloadOption>>> {
        return withContext(Dispatchers.IO) {
            try {
                if (!newPipeInitialized) {
                    val localization = Localization.DEFAULT
                    NewPipe.init(CustomDownloader.getInstance(), localization)
                    newPipeInitialized = true
                }
                Log.d("SearchDetailFragment", "Link analiz edilir: $url")
                val service = NewPipe.getServiceByUrl(url)
                if (service == null) {
                    Log.w("SearchDetailFragment", "Bu URL üçün servis tapılmadı: $url")
                    return@withContext Result.failure(Exception("Dəstəklənməyən URL."))
                }
                val streamInfo: StreamInfo = StreamInfo.getInfo(service, url)
                Log.d("SearchDetailFragment", "Məlumat alındı: ${streamInfo.name}")
                val options = mutableListOf<DownloadOption>()
                streamInfo.audioStreams?.forEach { audio ->
                    if (!audio.url.isNullOrEmpty()) {
                        val formatSuffix = audio.format?.suffix ?: "audio"
                        val quality = "$formatSuffix - ${audio.averageBitrate}kbps"
                        options.add(DownloadOption("Audio", quality, audio.url!!))
                    }
                }
                streamInfo.videoStreams?.forEach { video ->
                    if (!video.url.isNullOrEmpty() && video.isVideoOnly.not()) {
                        val formatSuffix = video.format?.suffix ?: "video"
                        val quality = "${video.resolution} - $formatSuffix"
                        options.add(DownloadOption("Video", quality, video.url!!))
                    }
                }
                if (options.isEmpty()) {
                    Log.w("SearchDetailFragment", "Yüklənə bilən axın tapılmadı: $url")
                    return@withContext Result.failure(Exception("Yüklənə bilən format tapılmadı."))
                }
                Result.success(Pair(streamInfo, options))
            } catch (e: Exception) {
                Log.e("SearchDetailFragment", "parseUrlAndGetOptions xətası", e)
                Result.failure(e)
            }
        }
    }

    private fun startDownload(option: DownloadOption) {
        Log.d("SearchDetailFragment", "startDownload çağırıldı (amma MediaPreviewFragment-də olmalıdır): ${option.directUrl}")
    }

    private fun DownloadOption.mimeType(actualExtension: String): String {
        return when (actualExtension) {
            "mp3" -> "audio/mpeg"; "m4a" -> "audio/mp4"; "opus" -> "audio/opus"; "ogg" -> "audio/ogg"
            "mp4" -> "video/mp4"; "webm" -> "video/webm"
            else -> if (this.format.contains("Audio", ignoreCase = true)) "audio/*" else if (this.format.contains("Video", ignoreCase = true)) "video/*" else "*/*"
        }
    }

    private fun DownloadOptionsAdapter.submitList(list: List<DownloadOption>) {

    }

    override fun onDetach() {
        super.onDetach()
        searchDetailListener = null
    }
}
