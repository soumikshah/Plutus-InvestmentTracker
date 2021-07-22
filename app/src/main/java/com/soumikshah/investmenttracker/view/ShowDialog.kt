package com.soumikshah.investmenttracker.view

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup
import java.text.SimpleDateFormat
import java.util.*

class ShowDialog internal constructor(shouldUpdate: Boolean, investment: Investment?, position: Int): Fragment() {
    private var inputInvestmentName: TextInputEditText? = null
    private var inputInvestmentAmount: TextInputEditText? = null
    private var inputInvestmentPercent: TextInputEditText? = null
    private var inputInvestmentMedium: TextInputEditText? = null
    private var inputInvestmentCategory: TextInputEditText? = null
    private var inputInvestmentDate:TextView? = null
    private var inputInvestNumberOfUnitsHeld: TextInputEditText? = null
    private var inputInvestPricePerUnit: TextInputEditText? = null
    private var inputInvestmentNumberOfMonths: TextInputEditText? = null
    private var buttonGroup: ThemedToggleButtonGroup? = null
    private var inrButton:ThemedButton? = null
    private var dollarButton: ThemedButton? = null
    private var currency:String? = null
    private var dialogTitle:TextView? = null
    private var shouldUpdate:Boolean = false
    private var datePickerDialog: DatePickerDialog? = null
    private var investmentDateInLong: Long = 0
    private var investment:Investment? = null
    private var positionOfTheInvestment: Int? = null
    private var interestToBeReceived = 0f
    private var positiveButton: Button? = null
    private var negativeButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.investment_dialog, container, false)
        inputInvestmentName = view.findViewById(R.id.investment)
        inputInvestmentAmount = view.findViewById(R.id.investmentAmount)
        inputInvestmentPercent = view.findViewById(R.id.investmentInterest)
        inputInvestmentMedium = view.findViewById(R.id.investmentMedium)
        inputInvestmentCategory = view.findViewById(R.id.investmentCategory)
        inputInvestmentDate = view.findViewById(R.id.investedDate)
        inputInvestNumberOfUnitsHeld = view.findViewById(R.id.investmentNumberOfUnits)
        inputInvestPricePerUnit = view.findViewById(R.id.investmentPricePerUnit)
        inputInvestmentNumberOfMonths = view.findViewById(R.id.investedNumberOfMonths)
        buttonGroup = view.findViewById(R.id.toggleGroup)
        inrButton = view.findViewById(R.id.rupee)
        dollarButton = view.findViewById(R.id.dollar)
        dialogTitle = view.findViewById(R.id.dialog_title)
        positiveButton = view.findViewById(R.id.positiveButton)
        negativeButton = view.findViewById(R.id.negativeButton)
        //Select INR button by default
        if(investment== null){
            buttonGroup!!.selectButton(inrButton!!)
        }else{
            if(investment!!.investmentCurrency.equals(getString(R.string.inr))){
                currency = getString(R.string.inr)
                buttonGroup!!.selectButton(inrButton!!)
            }else if (investment!!.investmentCurrency.equals(getString(R.string.usd))){
                currency = getString(R.string.usd)
                buttonGroup!!.selectButton(dollarButton!!)
            }
        }
        dialogTitle!!.text = if (!shouldUpdate) getString(R.string.new_investment_title)
        else getString(R.string.edit_investment_title)

        inputInvestmentDate!!.setOnClickListener { // calender class's instance and get current date , month and year from calender
            val c = Calendar.getInstance()
            val mYear = c[Calendar.YEAR] // current year
            val mMonth = c[Calendar.MONTH] // current month
            val mDay = c[Calendar.DAY_OF_MONTH] // current day
            // date picker dialog
            datePickerDialog = DatePickerDialog(requireContext(), R.style.DatePickerTheme,
                { _, year, monthOfYear, dayOfMonth -> // set day of month , month and year value in the edit text
                    inputInvestmentDate!!.text = String.format(Locale.ENGLISH, "%d/%d/%d", dayOfMonth,
                        monthOfYear + 1, year)
                    investmentDateInLong = c.timeInMillis
                }, mYear, mMonth, mDay)
            datePickerDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.DKGRAY))
            datePickerDialog!!.show()
        }

        if (shouldUpdate) {
            showEditedInvestment(investment!!)
            positiveButton!!.text = getString(R.string.edit)
        }
        negativeButton!!.setOnClickListener { activity?.onBackPressed() }

        positiveButton!!.setOnClickListener {
            if (TextUtils.isEmpty(inputInvestmentName!!.text.toString())) {
                Toast.makeText(requireContext(), "Enter Investment Name!", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(inputInvestmentAmount!!.text.toString())) {
                Toast.makeText(requireContext(), "Enter Investment Amount!", Toast.LENGTH_LONG).show()
            } else if (TextUtils.isEmpty(inputInvestmentCategory!!.text.toString())) {
                Toast.makeText(requireContext(), "Enter Investment Type!", Toast.LENGTH_LONG).show()
            }
            if(shouldUpdate && investment != null){
                editInvestment()
            }else{
                addInvestment()
            }
            activity?.onBackPressed()
        }
        return view
    }

    private fun showEditedInvestment(investment:Investment ){
            inputInvestmentName!!.setText(investment.investmentName.toString())
            inputInvestmentAmount!!.setText(investment.investmentAmount.toString())
            inputInvestmentPercent!!.setText(investment.investmentPercent.toString())
            inputInvestmentMedium!!.setText(investment.investmentMedium.toString())
            inputInvestmentCategory!!.setText(investment.investmentCategory.toString())
            if(inputInvestmentDate!!.text.isNotBlank()){
                val sim = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                inputInvestmentDate!!.text = sim.format(investment.investmentDate)
            }
            inputInvestmentNumberOfMonths!!.setText(investment.investmentMonth.toString())
            inputInvestNumberOfUnitsHeld!!.setText(investment.investmentNumberOfUnits.toString())
            inputInvestPricePerUnit!!.setText(investment.investmentPricePerUnit.toString())

            if(inrButton!!.isSelected){
                currency = getString(R.string.inr)
            }else if(dollarButton!!.isSelected){
                currency = getString(R.string.usd)
            }
    }

    private fun addInvestment(){

        interestToBeReceived = if (inputInvestmentPercent!!.text.toString().matches("".toRegex())) {
            0f
        } else {
            inputInvestmentPercent!!.text.toString().toFloat()
        }
        if (inputInvestmentMedium!!.text.toString().isEmpty()) {
            inputInvestmentMedium!!.setText(R.string.not_mentioned)
        }
        if (inputInvestmentCategory!!.text.toString().isEmpty()) {
            inputInvestmentCategory!!.setText("")
        }
        if (inputInvestmentNumberOfMonths!!.text.toString().isEmpty()) {
            inputInvestmentNumberOfMonths!!.setText("0")
        }
        if(inputInvestNumberOfUnitsHeld!!.text.toString().isEmpty()){
            inputInvestNumberOfUnitsHeld!!.setText("")
        }
        if(inputInvestPricePerUnit!!.text.toString().isEmpty()){
            inputInvestPricePerUnit!!.setText("0")
        }
        if(inrButton!!.isSelected){
            currency = getString(R.string.inr)
        }else if(dollarButton!!.isSelected){
            currency = getString(R.string.usd)
        }else{
            currency = getString(R.string.inr)
        }
        (activity as MainActivity).mainFragment!!.investmentHelper!!.createInvestment(inputInvestmentName!!.text.toString(),
            inputInvestmentAmount!!.text.toString().toInt(),
            interestToBeReceived,
            inputInvestmentMedium!!.text.toString(),
            inputInvestmentCategory!!.text.toString(),
            investmentDateInLong,
            inputInvestmentNumberOfMonths!!.text.toString().toInt(),
            inputInvestNumberOfUnitsHeld!!.text.toString(),
            inputInvestPricePerUnit!!.text.toString().toInt(),
            currency)
        //activity?.onBackPressed()
    }

    private fun editInvestment(){
        interestToBeReceived = if (inputInvestmentPercent!!.text.toString().matches("".toRegex())) {
            0f
        } else {
            inputInvestmentPercent!!.text.toString().toFloat()
        }

        if (inputInvestmentMedium!!.text.toString().isEmpty()) {
            inputInvestmentMedium!!.setText(R.string.not_mentioned)
        }
        if (inputInvestmentCategory!!.text.toString().isEmpty()) {
            inputInvestmentCategory!!.setText("")
        }
        if (inputInvestmentNumberOfMonths!!.text.toString().isEmpty()) {
            inputInvestmentNumberOfMonths!!.setText("0")
        }
        if(inputInvestNumberOfUnitsHeld!!.text.toString().isEmpty()){
            inputInvestNumberOfUnitsHeld!!.setText("")
        }
        if(inputInvestPricePerUnit!!.text.toString().isEmpty()){
            inputInvestPricePerUnit!!.setText("0")
        }

        (activity as MainActivity).mainFragment!!.investmentHelper!!.updateInvestment(inputInvestmentName!!.text.toString(),
            inputInvestmentAmount!!.text.toString().toInt(),
            interestToBeReceived,
            inputInvestmentMedium!!.text.toString(),
            inputInvestmentCategory!!.text.toString(),
            investmentDateInLong,
            inputInvestmentNumberOfMonths!!.text.toString().toInt(),
            inputInvestNumberOfUnitsHeld!!.text.toString(),
            inputInvestPricePerUnit!!.text.toString().toInt(),
            currency,
            positionOfTheInvestment!!)
    }

    init {
        this.shouldUpdate = shouldUpdate
        this.investment = investment
        this.positionOfTheInvestment = position
    }
}