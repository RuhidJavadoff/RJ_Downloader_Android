package com.ruhidjavadoff.rjdownloader

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity(),
    FooterFragment.OnFooterInteractionListener,
    HeaderFragment.OnHeaderInteractionListener,
    BodyFragment.OnBodyInteractionListener,
    SearchDetailFragment.OnSearchDetailInteractionListener {

    private val headerFragment: HeaderFragment?
        get() = supportFragmentManager.findFragmentById(R.id.header_fragment_container) as? HeaderFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {

            supportFragmentManager.beginTransaction()
                .replace(R.id.body_fragment_container, BodyFragment())
                .commit()

        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStackImmediate()
                    updateHeaderAfterBackNavigation()
                } else {
                    finish()
                }
            }
        })
    }

    private fun updateHeaderAfterBackNavigation() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.body_fragment_container)
        when (currentFragment) {
            is BodyFragment -> headerFragment?.showTabs()
            is SearchDetailFragment -> headerFragment?.hideHeader()
            is SettingsFragment -> headerFragment?.showTitle(getString(R.string.footer_settings), showSettingsButton = false)
            is MediaPreviewFragment -> {
                val title = currentFragment.arguments?.getString("media_title") ?: getString(R.string.title_media_preview)
                headerFragment?.showTitle(title, showSettingsButton = false)
            }
            is DownloadsFragment -> headerFragment?.showTitle(getString(R.string.title_downloads_page), showSettingsButton = true) // DÜZƏLİŞ: showSettingsButton = false edile biler
            else -> {
                if (currentFragment == null || currentFragment is BodyFragment && supportFragmentManager.backStackEntryCount == 0) {
                    headerFragment?.showTabs()
                }
            }
        }
    }

    override fun onNavigateTo(destination: String) {
        when (destination) {
            "DOWNLOAD" -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.body_fragment_container)
                if (currentFragment !is BodyFragment || supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    navigateToFragment(BodyFragment(), addToBackStack = false)
                }
                headerFragment?.showTabs()
            }
            "DOWNLOADS_PAGE" -> {
                navigateToFragment(DownloadsFragment())
                headerFragment?.showTitle(getString(R.string.title_downloads_page), showSettingsButton = true)
            }
            "SETTINGS" -> {
                navigateToFragment(SettingsFragment())
                headerFragment?.showTitle(getString(R.string.footer_settings), showSettingsButton = false)
            }
            "PLAY" -> {
                Toast.makeText(this, "İfa/Çal səhifəsi hələ hazır deyil", Toast.LENGTH_SHORT).show()
            }
            "DOWNLOADS_PAGE" -> {
                navigateToFragment(DownloadsFragment())
                headerFragment?.showTitle(getString(R.string.title_downloads_page), showSettingsButton = true) // DÜZƏLİŞ: showSettingsButton = true

            }
        }
    }

    override fun onTabSelected(tabTitle: String) { // From HeaderFragment (Tab mode)
        if (tabTitle == getString(R.string.tab_search)) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.body_fragment_container)
            if (currentFragment !is BodyFragment && currentFragment !is SearchDetailFragment && currentFragment !is MediaPreviewFragment) {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                navigateToFragment(BodyFragment(), addToBackStack = false)
                headerFragment?.showTabs()
            }
        } else {
            Toast.makeText(this, "$tabTitle tabı seçildi (hələlik boş)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackButtonPressed() { // From HeaderFragment (Title mode)
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onSettingsButtonPressedInHeader() { // From HeaderFragment (Title mode - settings icon)
        if (supportFragmentManager.findFragmentById(R.id.body_fragment_container) !is SettingsFragment) {
            navigateToFragment(SettingsFragment())
            headerFragment?.showTitle(getString(R.string.footer_settings), showSettingsButton = false)
        }
    }

    override fun onSearchButtonClicked() { // From BodyFragment
        headerFragment?.hideHeader()
        navigateToFragment(SearchDetailFragment())
    }

    override fun onMediaReadyForPreview( // From SearchDetailFragment
        title: String?,
        thumbnailUrl: String?,
        downloadOptions: List<DownloadOption>
    ) {
        val mediaPreviewFragment = MediaPreviewFragment().apply {
            arguments = Bundle().apply {
                putString("media_title", title ?: getString(R.string.media_preview_title_placeholder))
                putString("media_thumbnail_url", thumbnailUrl)
                putParcelableArrayList("download_options", ArrayList(downloadOptions))
            }
        }
        headerFragment?.showTitle(title ?: getString(R.string.title_media_preview), showSettingsButton = false)
        navigateToFragment(mediaPreviewFragment)
    }

    private fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.body_fragment_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(fragment::class.java.simpleName)
        }
        transaction.commit()
        // Fragment dəyişdikdən dərhal sonra header-i yeniləmək üçün burada da çağırış etmək olar,
        // amma onBackPressedDispatcher və naviqasiya metodlarında etmək daha yaxşıdır.
        // Məsələn: updateHeaderAfterNavigation(fragment)
    }
}
