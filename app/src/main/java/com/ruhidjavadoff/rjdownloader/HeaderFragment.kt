package com.ruhidjavadoff.rjdownloader

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
// import android.widget.Toast // indi Toast istifadə etmedim istesen deyis
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout

class HeaderFragment : Fragment() {

    interface OnHeaderInteractionListener {
        fun onTabSelected(tabTitle: String)
        fun onBackButtonPressed()
        fun onSettingsButtonPressedInHeader() // YENİ
    }

    private var listener: OnHeaderInteractionListener? = null

    private lateinit var tabLayout: TabLayout
    private lateinit var titleHeaderContainer: RelativeLayout
    private lateinit var tvHeaderTitle: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var btnHeaderSettings: ImageButton // YENİ

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHeaderInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnHeaderInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_header, container, false)
        tabLayout = view.findViewById(R.id.tabLayout)
        titleHeaderContainer = view.findViewById(R.id.titleHeaderContainer)
        tvHeaderTitle = view.findViewById(R.id.tvHeaderTitle)
        btnBack = view.findViewById(R.id.btnBack)
        btnHeaderSettings = view.findViewById(R.id.btnHeaderSettings) // YENİ

        setupTabs()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                listener?.onTabSelected(tab?.text.toString())
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        btnBack.setOnClickListener {
            listener?.onBackButtonPressed()
        }

        btnHeaderSettings.setOnClickListener { // YENİ
            listener?.onSettingsButtonPressedInHeader()
        }
    }

    private fun setupTabs() {
        if (tabLayout.tabCount == 0) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_search))
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_youtube))
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_music))
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_other))
        }
    }

    fun showTabs() {
        tabLayout.visibility = View.VISIBLE
        titleHeaderContainer.visibility = View.GONE
        btnHeaderSettings.visibility = View.GONE
    }

    fun showTitle(title: String, showSettingsButton: Boolean = false) {
        tabLayout.visibility = View.GONE
        titleHeaderContainer.visibility = View.VISIBLE
        tvHeaderTitle.text = title
        btnHeaderSettings.visibility = if (showSettingsButton) View.VISIBLE else View.GONE
    }

    fun hideHeader() {
        tabLayout.visibility = View.GONE
        titleHeaderContainer.visibility = View.GONE
        btnHeaderSettings.visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}