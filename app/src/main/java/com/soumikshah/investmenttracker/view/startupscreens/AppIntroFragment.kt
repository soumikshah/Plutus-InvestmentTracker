package com.soumikshah.investmenttracker.view.startupscreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.view.MainActivity

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
        val view = inflater.inflate(R.layout.fragment_intro_screen,container,false)
        nextButton = view.findViewById(R.id.nextButton)
        (activity as MainActivity).hideFab()
        (activity as MainActivity).hideBottomNav()
        nextButton!!.setOnClickListener {
            (activity as MainActivity).replaceFragment(EmptyViewFragment())
        }
        return view
    }
}