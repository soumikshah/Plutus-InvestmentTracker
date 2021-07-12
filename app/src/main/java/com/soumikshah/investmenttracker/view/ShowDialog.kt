package com.soumikshah.investmenttracker.view

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import kotlinx.android.synthetic.main.activity_main.*
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup
import java.text.SimpleDateFormat
import java.util.*

class ShowDialog internal constructor(shouldUpdate: Boolean, investment: Investment?, position: Int): Fragment() {
    private var inputInvestmentName: EditText? = null
    private var inputInvestmentAmount: EditText? = null
    private var inputInvestmentPercent: EditText? = null
    private var inputInvestmentMedium: EditText? = null
    private var inputInvestmentCategory: EditText? = null
    private var inputInvestmentDate:TextView? = null
    private var inputInvestNumberOfUnitsHeld: EditText? = null
    private var inputInvestPricePerUnit: EditText? = null
    private var inputInvestmentNumberOfMonths: EditText? = null
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.investment_dialog, container,false)
        val alertDialogBuilderUserInput = AlertDialog.Builder(requireContext())
        alertDialogBuilderUserInput.setView(view)
        inputInvestmentName = view.findViewById(R.id.investment)
        inputInvestmentAmount = view.findViewById(R.id.investmentAmount)
        inputInvestmentPercent = view.findViewById(R.id.investmentInterest)
        inputInvestmentMedium = view.findViewById(R.id.investmentMedium)
        inputInvestmentCategory = view.findViewById(R.id.investmentCategory)
        inputInvestmentDate = view.findViewById(R.id.investedDate)
        inputInvestNumberOfUnitsHeld = view.findViewById(R.id.investmentNumberOfUnits)
        inputInvestPricePerUnit = view.findViewById(R.id.investmentPricePerUnit)
        inputInvestmentAmount = view.findViewById(R.id.investedNumberOfMonths)
        buttonGroup = view.findViewById(R.id.toggleGroup)
        inrButton = view.findViewById(R.id.rupee)
        dollarButton = view.findViewById(R.id.dollar)
        dialogTitle = view.findViewById(R.id.dialog_title)

        //Select INR button by default
        buttonGroup!!.selectButton(inrButton!!)
        dialogTitle!!.text = if (!shouldUpdate) getString(R.string.new_investment_title)
                            else getString(R.string.edit_investment_title)

        inputInvestmentDate!!.setOnClickListener { // calender class's instance and get current date , month and year from calender
            val c = Calendar.getInstance()
            val mYear = c[Calendar.YEAR] // current year
            val mMonth = c[Calendar.MONTH] // current month
            val mDay = c[Calendar.DAY_OF_MONTH] // current day
            // date picker dialog
            datePickerDialog = DatePickerDialog(requireContext(), R.style.DatePickerTheme,
                { view, year, monthOfYear, dayOfMonth -> // set day of month , month and year value in the edit text
                    inputInvestmentDate!!.text = String.format(Locale.ENGLISH, "%d/%d/%d", dayOfMonth,
                        monthOfYear + 1, year)
                    investmentDateInLong = c.timeInMillis
                }, mYear, mMonth, mDay)
            datePickerDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.DKGRAY))
            datePickerDialog!!.show()
        }

        if (shouldUpdate) {
            editInvestmentData(investment!!)
        }
        alertDialogBuilderUserInput
            .setCancelable(false)
            .setPositiveButton(if (shouldUpdate) getString(R.string.update) else getString(R.string.save)) { _, _ -> }
            .setNegativeButton(getString(R.string.cancel)
            ) { dialogBox, _ -> dialogBox.cancel() }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK) {
                alertDialog.dismiss()
            }
            false
        }
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener { // Show toast message when no text is entered
            if (TextUtils.isEmpty(inputInvestmentName!!.text.toString())) {
                Toast.makeText(requireContext(), "Enter Investment Name!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else if (TextUtils.isEmpty(inputInvestmentAmount!!.text.toString())) {
                Toast.makeText(requireContext(), "Enter Investment Amount!", Toast.LENGTH_LONG).show()
                return@OnClickListener
            } else if (TextUtils.isEmpty(inputInvestmentCategory!!.text.toString())) {
                Toast.makeText(requireContext(), "Enter Investment Type!", Toast.LENGTH_LONG).show()
            } else {
                alertDialog.dismiss()
            }
            addInvestment()
            if (shouldUpdate && investment != null) {
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
            } else {
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
            }
        })
//        (activity as MainActivity).viewpager!!.adapter!!.notifyDataSetChanged()
        return view
    }

    private fun editInvestmentData(investment:Investment ){
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
    }

    init {
        this.shouldUpdate = shouldUpdate
        this.investment = investment
        this.positionOfTheInvestment = position
    }
}