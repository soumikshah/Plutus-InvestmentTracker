package com.soumikshah.investmenttracker.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.mynameismidori.currencypicker.CurrencyPicker
import com.soumikshah.investmenttracker.R

class EmptyViewFragment internal constructor(): Fragment() {
    private var currencyFirst: Button? = null
    private var currencySecond: Button? = null
    private var enableCurrencySecond: CheckBox? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_empty_view, container, false)
        currencyFirst = view.findViewById(R.id.currency1)
        currencySecond = view.findViewById(R.id.currency2)
        enableCurrencySecond = view.findViewById(R.id.enableCurrency2)
        (activity as MainActivity).hideFab()
        currencyFirst!!.setOnClickListener {
            val picker = CurrencyPicker.newInstance("Select Currency") // dialog title

            picker.setListener { name, code, symbol, flagDrawableResID ->
                // Implement your code here
                (activity as MainActivity).mainFragment!!.setCurrency(code)
                currencyFirst!!.text = code
                Log.d("Plutus","$name + $code + $symbol")
                picker.dismiss()
            }
            picker.show(requireFragmentManager(), "CURRENCY_PICKER")
        }
        enableCurrencySecond!!.setOnClickListener {
            currencySecond!!.isEnabled = enableCurrencySecond!!.isChecked
        }
        currencySecond!!.setOnClickListener {
            val picker = CurrencyPicker.newInstance("Select Currency") // dialog title

            picker.setListener { name, code, symbol, flagDrawableResID ->
                // Implement your code here
                (activity as MainActivity).mainFragment!!.setCurrency(code)
                currencySecond!!.text = code
                Log.d("Plutus","$name + $code + $symbol")
                picker.dismiss()
            }
            picker.show(requireFragmentManager(), "CURRENCY_PICKER")
        }


        return view
    }

}