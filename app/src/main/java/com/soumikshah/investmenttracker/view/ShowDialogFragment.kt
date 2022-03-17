package com.soumikshah.investmenttracker.view

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.content.DialogInterface
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.view.animation.Animation
import android.view.animation.AnimationUtils


class ShowDialogFragment internal constructor(shouldUpdate: Boolean, investment: Investment?, position: Int): Fragment() {
    private var inputInvestmentName: TextInputEditText? = null
    private var inputInvestmentAmount: TextInputEditText? = null
    private var inputInvestmentPercent: TextInputEditText? = null
    private var inputInvestmentMedium: AutoCompleteTextView? = null
    private var inputInvestmentCategory: AutoCompleteTextView? = null
    private var inputInvestmentDate:TextView? = null
    private var inputInvestNumberOfUnitsHeld: TextInputEditText? = null
    private var inputInvestPricePerUnit: TextInputEditText? = null
    private var inputInvestmentNumberOfMonths: TextInputEditText? = null
    private var buttonGroup: ThemedToggleButtonGroup? = null
    private var firstCurrencyButton:ThemedButton? = null
    private var secondCurrencyButton: ThemedButton? = null
    private var deleteButton:Button? = null
    private var currency:String? = null
    private var firstCurrency:String? = null
    private var secondCurrency:String? = null
    private var dialogTitle:TextView? = null
    private var shouldUpdate:Boolean = false
    private var datePickerDialog: DatePickerDialog? = null
    private var investmentDateInLong: Long = 0
    private var investment:Investment? = null
    private var positionOfTheInvestment: Int? = null
    private var interestToBeReceived = 0f
    private var positiveButton: Button? = null
    private var negativeButton: Button? = null
    private var backButton:Button? = null
    private var investmentIDBeforeEdit: Int? = 0
    private var investmentCategoryList:ArrayList<String>? = null
    private var investmentMediumList: ArrayList<String>? = null

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
        deleteButton = view.findViewById(R.id.delete_button)
        buttonGroup = view.findViewById(R.id.toggleGroup)
        firstCurrencyButton = view.findViewById(R.id.firstbutton)
        secondCurrencyButton = view.findViewById(R.id.secondbutton)
        dialogTitle = view.findViewById(R.id.dialog_title)
        positiveButton = view.findViewById(R.id.positiveButton)
        negativeButton = view.findViewById(R.id.negativeButton)
        backButton = view.findViewById(R.id.backButton)
        investmentCategoryList = (activity as MainActivity?)!!.mainFragment!!.investmentHelper!!.investmentCategory
        investmentMediumList = (activity as MainActivity?)!!.mainFragment!!.investmentHelper!!.investmentMedium
        val investmentCategoryAdapter= ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, investmentCategoryList!!.distinct())
        val investmentMediumAdapter= ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, investmentMediumList!!.distinct())

        inputInvestmentCategory!!.threshold = 1
        inputInvestmentCategory!!.setAdapter(investmentCategoryAdapter)

        inputInvestmentMedium!!.threshold = 1
        inputInvestmentMedium!!.setAdapter(investmentMediumAdapter)

        (activity as MainActivity).hideFab()
        firstCurrency = (activity as MainActivity).mainFragment!!.getCurrency()
        secondCurrency = (activity as MainActivity).mainFragment!!.getCurrency2()
        if(secondCurrency.isNullOrEmpty()){
            secondCurrencyButton!!.visibility = GONE
        }
        firstCurrencyButton!!.text = firstCurrency.toString()

        if(!secondCurrency.isNullOrEmpty()){
            secondCurrencyButton!!.text = secondCurrency.toString()
        }
                //Select first button by default
        if(investment== null){
            buttonGroup!!.selectButton(firstCurrencyButton!!)
        }else{
            if(investment!!.investmentCurrency.equals(firstCurrency)){
                currency = firstCurrency
                buttonGroup!!.selectButton(firstCurrencyButton!!)
            }else if (!secondCurrency.isNullOrEmpty() && investment!!.investmentCurrency.equals(secondCurrency)){
                currency = secondCurrency
                buttonGroup!!.selectButton(secondCurrencyButton!!)
            }
        }
        dialogTitle!!.text = if (!shouldUpdate) getString(R.string.new_investment_title)
        else getString(R.string.edit_investment_title)

        if(dialogTitle!!.text.equals(getString(R.string.edit_investment_title))){
            deleteButton!!.visibility = View.VISIBLE
            val shake: Animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.shake)
            deleteButton!!.startAnimation(shake)
        }else{
            deleteButton!!.visibility = GONE
        }

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
        }
        negativeButton!!.setOnClickListener { activity?.onBackPressed() }

        positiveButton!!.setOnClickListener {
            if (TextUtils.isEmpty(inputInvestmentName!!.text.toString())) {
                Toast.makeText(requireContext(), "Enter Investment Name!", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(inputInvestmentAmount!!.text.toString())) {
                Toast.makeText(requireContext(), "Enter Investment Amount!", Toast.LENGTH_LONG).show()
            } else if (TextUtils.isEmpty(inputInvestmentCategory!!.text.toString())) {
                Toast.makeText(requireContext(), "Enter Investment Type!", Toast.LENGTH_LONG).show()
            }else if(TextUtils.isEmpty(inputInvestmentMedium!!.text.toString())){
                Toast.makeText(requireContext(),"Please insert how did you invest the money",Toast.LENGTH_LONG).show()
            } else{
                if(shouldUpdate && investment != null){
                    editInvestment()
                    (activity as? MainActivity)!!.updateViewPager()
                    //When save button is pressed, it takes user to homepage
                    // and clears all the backstack of fragments.
                    clearBackStack()
                }else{
                    addInvestment()
                    (activity as? MainActivity)!!.updateViewPager()
                    //When save button is pressed, it takes user to homepage
                    // and clears all the backstack of fragments.
                    clearBackStack()
                }
            }
        }

        deleteButton!!.setOnClickListener{
            deleteDialog()
        }

        backButton!!.setOnClickListener {
            activity?.onBackPressed()
        }
        return view
    }

    private fun clearBackStack() {
        val manager: FragmentManager = (activity as? MainActivity)!!.supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            val first: FragmentManager.BackStackEntry = manager.getBackStackEntryAt(0)
            manager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    private fun deleteDialog() {
        val context: Context = ContextThemeWrapper(requireContext(), R.style.DialogboxTheme)
        val builder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            .setMessage(getString(R.string.delete_investment_title))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                deleteInvestment(investment!!)
                dialog.dismiss()
                (activity as? MainActivity)!!.updateViewPager()
                activity?.onBackPressed()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
        val nbutton: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        nbutton.setBackgroundColor(Color.GREEN)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(40, 0, 0, 0)
        val pbutton: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        pbutton.setBackgroundColor(Color.RED)
        pbutton.layoutParams = params
    }
    private fun showEditedInvestment(investment:Investment ){
            investmentIDBeforeEdit = investment.id
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

            if(firstCurrencyButton!!.isSelected){
                currency =firstCurrency
            }else if(secondCurrencyButton!!.isSelected){
                currency = secondCurrency
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
        currency = when {
            firstCurrencyButton!!.isSelected -> {
                firstCurrency
            }
            secondCurrencyButton!!.isSelected -> {
                secondCurrency
            }
            else -> {
                firstCurrency
            }
        }

        (activity as MainActivity).mainFragment!!.investmentHelper!!.createInvestment(inputInvestmentName!!.text.toString(),
            inputInvestmentAmount!!.text.toString().toFloat(),
            interestToBeReceived,
            inputInvestmentMedium!!.text.toString(),
            inputInvestmentCategory!!.text.toString(),
            investmentDateInLong,
            inputInvestmentNumberOfMonths!!.text.toString().toInt(),
            inputInvestNumberOfUnitsHeld!!.text.toString(),
            inputInvestPricePerUnit!!.text.toString().toFloat(),
            currency)
    }

    private fun deleteInvestment(investment: Investment){
        (activity as MainActivity).mainFragment!!.investmentHelper!!.deleteInvestment(investment)
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

        currency = when {
            firstCurrencyButton!!.isSelected -> {
                firstCurrency
            }
            secondCurrencyButton!!.isSelected -> {
                secondCurrency
            }
            else -> {
                firstCurrency
            }
        }

        (activity as MainActivity).mainFragment!!.investmentHelper!!.updateInvestment(investmentIDBeforeEdit!!,
            inputInvestmentName!!.text.toString(),
            inputInvestmentAmount!!.text.toString().toFloat(),
            interestToBeReceived,
            inputInvestmentMedium!!.text.toString(),
            inputInvestmentCategory!!.text.toString(),
            investmentDateInLong,
            inputInvestmentNumberOfMonths!!.text.toString().toInt(),
            inputInvestNumberOfUnitsHeld!!.text.toString(),
            inputInvestPricePerUnit!!.text.toString().toFloat(),
            currency,
            positionOfTheInvestment!!)
    }


    override fun onDestroy() {
        (activity as MainActivity).showFab()
        super.onDestroy()
    }

    init {
        this.shouldUpdate = shouldUpdate
        this.investment = investment
        this.positionOfTheInvestment = position
    }
}