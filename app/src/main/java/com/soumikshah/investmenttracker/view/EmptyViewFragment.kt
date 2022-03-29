package com.soumikshah.investmenttracker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mynameismidori.currencypicker.CurrencyPicker
import com.soumikshah.investmenttracker.R

class EmptyViewFragment internal constructor(): Fragment() {
    private var currencyFirst: Button? = null
    private var nextButton:Button? = null

    override fun onResume() {
        (activity as MainActivity).hideFab()
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_empty_view, container, false)
        currencyFirst = view.findViewById(R.id.currency1)
        nextButton = view.findViewById(R.id.nextButton)

        (activity as MainActivity).hideFab()
        (activity as MainActivity).hideBottomNav()

        currencyFirst!!.setOnClickListener {
            val picker = CurrencyPicker.newInstance("Select Currency") // dialog title
            picker.setListener { _, code, symbol, _ ->
                currencyFirst!!.text = code
                val symbolCurrency = (activity as MainActivity).mainFragment!!.fetchCurrencySymbol(currencyFirst!!.text.toString())
                if(symbolCurrency!=null){
                    ((activity as MainActivity).mainFragment!!.setCurrencySymbol(symbolCurrency))
                }else{
                    ((activity as MainActivity).mainFragment!!.setCurrencySymbol(symbol.toString()))
                }

                nextButton!!.isEnabled = true
                nextButton!!.isClickable = true
                picker.dismiss()
            }
            picker.show(parentFragmentManager, "CURRENCY_PICKER")
        }

        nextButton!!.setOnClickListener {
            if( !currencyFirst!!.text.equals(getString(R.string.currency_1))){
                (activity as MainActivity).mainFragment!!.setCurrency(currencyFirst!!.text.toString())
                (activity as MainActivity).replaceFragment(ShowDialogFragment(false, null, -1))
            } else{
                Toast.makeText(requireContext(),"Please make sure you have selected currency!",Toast.LENGTH_LONG).show()
            }
        }
        return view
    }
}