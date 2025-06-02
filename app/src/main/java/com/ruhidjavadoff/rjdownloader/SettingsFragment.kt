package com.ruhidjavadoff.rjdownloader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    private lateinit var tvBetaMessage: TextView
    private lateinit var btnShareApp: Button
    private lateinit var btnOtherApps: Button
    private lateinit var tvAppVersion: TextView

    private val shareAppUrl = "https://ruhidjavadoff.site/app/md/"
    private val otherAppsUrl = "https://ruhidjavadoff.site/app/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        tvBetaMessage = view.findViewById(R.id.tvBetaMessage)
        btnShareApp = view.findViewById(R.id.btnShareApp)
        btnOtherApps = view.findViewById(R.id.btnOtherApps)
        tvAppVersion = view.findViewById(R.id.tvAppVersion)

        setupClickListeners()
        setAppVersion()

        return view
    }

    private fun setupClickListeners() {
        btnShareApp.setOnClickListener {
            shareApp()
        }

        btnOtherApps.setOnClickListener {
            openUrl(otherAppsUrl)
        }
    }

    private fun shareApp() {
        val shareText = "${getString(R.string.settings_share_text)} $shareAppUrl"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {

        }
    }

    private fun setAppVersion() {
        try {
            val versionName = requireContext().packageManager
                .getPackageInfo(requireContext().packageName, 0).versionName
            tvAppVersion.text = getString(R.string.settings_app_version, versionName)
        } catch (e: Exception) {
            tvAppVersion.text = getString(R.string.settings_app_version, "N/A")
        }
    }
}