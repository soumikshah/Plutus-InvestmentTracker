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
    private var currencySecond: Button? = null
    private var enableCurrencySecond: CheckBox? = null
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
        currencySecond = view.findViewById(R.id.currency2)
        enableCurrencySecond = view.findViewById(R.id.enableCurrency2)
        nextButton = view.findViewById(R.id.nextButton)

        val shake: Animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.shake)
        currencyFirst!!.startAnimation(shake)

        (activity as MainActivity).hideFab()
        (activity as MainActivity).hideBottomNav()

        currencyFirst!!.setOnClickListener {
            val picker = CurrencyPicker.newInstance("Select Currency") // dialog title
            picker.setListener { _, code, symbol, _ ->
                currencyFirst!!.text = code
                ((activity as MainActivity).mainFragment!!.setCurrencySymbol(symbol))
                nextButton!!.isEnabled = true
                picker.dismiss()
            }
            picker.show(parentFragmentManager, "CURRENCY_PICKER")
        }
        enableCurrencySecond!!.setOnClickListener {
            currencySecond!!.isEnabled = enableCurrencySecond!!.isChecked
            currencySecond!!.startAnimation(shake)
        }
        currencySecond!!.setOnClickListener {
            val picker = CurrencyPicker.newInstance("Select Currency") // dialog title

            picker.setListener { _, code, symbol, _ ->
                currencySecond!!.text = code
                ((activity as MainActivity).mainFragment!!.setCurrencySymbol2(symbol))
                picker.dismiss()
            }
            picker.show(parentFragmentManager, "CURRENCY_PICKER")
        }
        nextButton!!.setOnClickListener {
            if(!enableCurrencySecond!!.isChecked || (enableCurrencySecond!!.isChecked && !currencySecond!!.text.equals(getString(R.string.currency_2)))){
                (activity as MainActivity).mainFragment!!.setCurrency(currencyFirst!!.text.toString())

                if(currencySecond!!.isEnabled && !currencySecond!!.text.isNullOrEmpty()){
                    (activity as MainActivity).mainFragment!!.setCurrency2(currencySecond!!.text.toString())
                }
                (activity as MainActivity).loadFragment(ShowDialogFragment(false, null, -1))
            } else{
                Toast.makeText(requireContext(),"Please make sure you have selected currency!",Toast.LENGTH_LONG).show()
            }
        }
        return view
    }
}