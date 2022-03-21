package com.soumikshah.investmenttracker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.soumikshah.investmenttracker.R

class AppIntroFragment:Fragment() {

    var nextButton:Button?=null


    override fun onResume() {
        (activity as MainActivity).hideFab()
        (activity as MainActivity).hideBottomNav()
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_intro_screen1,container,false)
        nextButton = view.findViewById(R.id.nextButton)
        (activity as MainActivity).hideFab()
        (activity as MainActivity).hideBottomNav()
        nextButton!!.setOnClickListener {
            (activity as MainActivity).replaceFragment(EmptyViewFragment())
        }
        return view
    }
}