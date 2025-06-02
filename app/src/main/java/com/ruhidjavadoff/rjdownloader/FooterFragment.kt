package com.ruhidjavadoff.rjdownloader

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class FooterFragment : Fragment() {

    interface OnFooterInteractionListener {
        fun onNavigateTo(destination: String)
    }

    private var listener: OnFooterInteractionListener? = null

    private lateinit var navDownload: LinearLayout
    private lateinit var navMainDownloads: LinearLayout // YENİ
    private lateinit var navPlay: LinearLayout
    private lateinit var navSettings: LinearLayout

    // Düymə elementlərini saxlamaq üçün
    private val navButtons = mutableMapOf<View, Pair<ImageView, TextView>>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFooterInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFooterInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_footer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navDownload = view.findViewById(R.id.nav_download)
        navMainDownloads = view.findViewById(R.id.nav_main_downloads) // YENİ
        navPlay = view.findViewById(R.id.nav_play)
        navSettings = view.findViewById(R.id.nav_settings)


        navButtons[navDownload] = Pair(view.findViewById(R.id.icon_nav_download), view.findViewById(R.id.text_nav_download))
        navButtons[navMainDownloads] = Pair(view.findViewById(R.id.icon_nav_main_downloads), view.findViewById(R.id.text_nav_main_downloads))
        navButtons[navPlay] = Pair(view.findViewById(R.id.icon_nav_play), view.findViewById(R.id.text_nav_play))
        navButtons[navSettings] = Pair(view.findViewById(R.id.icon_nav_settings), view.findViewById(R.id.text_nav_settings))

        navDownload.setOnClickListener {
            listener?.onNavigateTo("DOWNLOAD") // Ana səhifə
            updateButtonSelection(it)
        }
        navMainDownloads.setOnClickListener { // YENİ
            listener?.onNavigateTo("DOWNLOADS_PAGE") // Yükləmələr səhifəsi
            updateButtonSelection(it)
        }
        navPlay.setOnClickListener {
            listener?.onNavigateTo("PLAY")
            updateButtonSelection(it)
        }
        navSettings.setOnClickListener {
            listener?.onNavigateTo("SETTINGS")
            updateButtonSelection(it)
        }

        // İlkin olaraq "Yüklə" düyməsini seçilmiş kimi göstər daima
        updateButtonSelection(navDownload)
    }

    // Düymənin rəngini dəyişən funksiya
    private fun updateButtonSelection(selectedButton: View) {
        val activeColor = ContextCompat.getColor(requireContext(), R.color.rj_purple)
        val inactiveColor = ContextCompat.getColor(requireContext(), R.color.rj_gray)

        navButtons.forEach { (buttonView, iconTextViewPair) ->
            val (icon, textView) = iconTextViewPair
            if (buttonView == selectedButton) {
                icon.setColorFilter(activeColor)
                textView.setTextColor(activeColor)
            } else {
                icon.setColorFilter(inactiveColor) //  ikon rəngini dəyişmək istəmirsense setiri sil, bu sətri silin
                textView.setTextColor(inactiveColor)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}