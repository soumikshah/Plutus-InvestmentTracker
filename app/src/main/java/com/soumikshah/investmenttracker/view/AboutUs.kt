package com.soumikshah.investmenttracker.view

import android.content.pm.PackageInfo
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.soumikshah.investmenttracker.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element


class AboutUs internal constructor(): Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val pInfo:PackageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName,0)
        return AboutPage(context)
            .isRTL(false)
            .setDescription(getString(R.string.app_description))
            .addItem(Element().setTitle("Version: ${pInfo.versionName}"))
            .addEmail("developer@soumikshah.com", "Email")
            .addFacebook("Droidgyaan","Facebook")
            .addGitHub("soumikshah","Github")
            .addTwitter("soumikshah","Twitter")
            .addWebsite("https://soumikshah.github.io/Plutus-InvestmentTracker/","Official website for Plutus")
            .addWebsite("https://f-droid.org/packages/com.soumikshah.investmenttracker/","Link to download Plutus")
            .setCustomFont(ResourcesCompat.getFont(requireContext(),R.font.robotoregular))
            .setImage(R.drawable.background_image)
            .create()
    }
}