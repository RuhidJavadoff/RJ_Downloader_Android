package com.ruhidjavadoff.rjdownloader

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class BodyFragment : Fragment() {


    interface OnBodyInteractionListener {
        fun onSearchButtonClicked()
    }

    private var listener: OnBodyInteractionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnBodyInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnBodyInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_body, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etLink = view.findViewById<EditText>(R.id.etLink)
        val btnFetch = view.findViewById<ImageButton>(R.id.btnFetch)

        // Axtarış düyməsinə basılanda...
        btnFetch.setOnClickListener {
            // ...MainActivity
            listener?.onSearchButtonClicked()
        }


        etLink.setOnClickListener {
            listener?.onSearchButtonClicked()
        }
    }



    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}