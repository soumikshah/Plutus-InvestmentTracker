package com.soumikshah.investmenttracker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.soumikshah.investmenttracker.R

class SettingsFragment internal constructor() : Fragment(){
    var sampleTextView:TextView? =null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        sampleTextView = view.findViewById(R.id.sampleTextView)
        sampleTextView!!.text = getString(R.string.sampletext)
        return view
    }

}