package com.soumikshah.investmenttracker.database

import android.content.Context
import com.soumikshah.investmenttracker.database.model.Investment
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class InvestmentHelper(var context: Context) {
    fun getInvestmentsList(): ArrayList<Investment> {
        return InvestmentsList
    }

    fun getInvestmentsListAccTOCurrency(currency: String):ArrayList<Investment>{
        val currencyInvestmentsList:ArrayList<Investment> = ArrayList()
        InvestmentsList.clear()
        InvestmentsList.addAll(db!!.allInvestments)
        for(investment in InvestmentsList){
           if(investment.investmentCurrency.equals(currency) &&
               (investment.investmentCategory.isNotEmpty()|| !investment.investmentCategory.isNullOrBlank())){
               currencyInvestmentsList.add(investment)
            }
        }
        return currencyInvestmentsList
    }



    private val InvestmentsList: ArrayList<Investment> = ArrayList()
    private val db: DatabaseHelper?
    private var nullDb = false
    val investmentTypeAndAmount: HashMap<String, Float> = HashMap()
    val investmentTypeAndAmountInCurrency: HashMap<String, Int> = HashMap()
    fun createInvestment(investmentName: String?,
                         investmentAmount: Float,
                         investmentPercent: Float,
                         investmentMedium: String?,
                         investmentCategory: String?,
                         investmentDate: Long,
                         investmentMonth: Int,
                         investmentNumberOfUnits:String,
                         investmentPricePerUnit:Float,
                         investmentCurrency:String?) {
        if (this.db != null) {
            val id = this.db.insertInvestment(investmentName, investmentAmount,
                    investmentPercent, investmentMedium, investmentCategory,
                    investmentDate, investmentMonth,investmentNumberOfUnits,
                    investmentPricePerUnit, investmentCurrency)

            // get the newly inserted note from db
            val n = db.getInvestment(id)
            if (n != null) {
                // adding new note to array list at 0 position
                InvestmentsList.add(0, n)
                // refreshing the list
                toggleEmptyInvestments()
            }
        }
    }

    fun updateInvestment(investmentId:Int,investment: String?, investmentAmount: Float,
                         investmentPercent: Float, investmentMedium: String?,
                         investmentCategory: String?,
                         investmentDate: Long,
                         investmentMonth: Int,
                         investmentNumberOfUnits: String,
                         investmentPricePerUnit: Float,
                         investmentCurrency: String?,
                         position: Int) {
        val n = InvestmentsList[position]
        n.id = investmentId
        n.investmentName = investment
        n.investmentAmount = investmentAmount
        n.investmentPercent = investmentPercent
        n.investmentMedium = investmentMedium
        n.investmentCategory = investmentCategory
        n.investmentDate = investmentDate
        n.investmentMonth = investmentMonth
        n.investmentNumberOfUnits = investmentNumberOfUnits
        n.investmentPricePerUnit = investmentPricePerUnit
        n.investmentCurrency = investmentCurrency

        // updating note in db
        db!!.updateInvestment(n)

        // refreshing the list
        InvestmentsList[position] = n
        toggleEmptyInvestments()
    }

    fun deleteInvestment(investment: Investment){
        db!!.deleteInvestment(investment)

        InvestmentsList.remove(investment)
        toggleEmptyInvestments()
    }
    fun deleteInvestment(position: Int) {
        // deleting the note from db
        db!!.deleteInvestment(InvestmentsList[position])

        // removing the note from the list
        InvestmentsList.removeAt(position)
        toggleEmptyInvestments()
    }

    private fun toggleEmptyInvestments() {
        nullDb = if (db!!.investmentCount > 0) {
            false
        } else {
            true
        }
    }

    val investmentTotalAmount: Float
        get() {
            var totalAmount:Float = 0.0F
            var investment: Investment
            for (i in getInvestmentsList().indices) {
                investment = getInvestmentsList()[i]
                if (investmentTypeAndAmount != null) {
                    if (!investmentTypeAndAmount.containsKey(investment.investmentCategory)) {
                        investmentTypeAndAmount[investment.investmentCategory] = investment.investmentAmount
                    } else if (investmentTypeAndAmount.containsKey(investment.investmentCategory)) {
                        investmentTypeAndAmount[investment.investmentCategory] = investmentTypeAndAmount[investment.investmentCategory]!! + investment.investmentAmount
                    }
                }
                totalAmount += investment.investmentAmount
            }
            return totalAmount
        }

    fun investmentTotalAmountWithCurrency(currency: String):Float{
        var totalAmount = 0.0f
        var investment:Investment
        investmentTypeAndAmount.clear()
        for (i in getInvestmentsList().indices) {
            investment = getInvestmentsList()[i]
            if(investment.investmentCurrency.equals(currency)){
                if (!investmentTypeAndAmount.containsKey(investment.investmentCategory)) {
                    investmentTypeAndAmount[investment.investmentCategory] = investment.investmentAmount
                } else if (investmentTypeAndAmount.containsKey(investment.investmentCategory)) {
                    investmentTypeAndAmount[investment.investmentCategory] = investmentTypeAndAmount[investment.investmentCategory]!! + investment.investmentAmount
                }
                totalAmount += investment.investmentAmount
            }
        }
        val bigDecimal = BigDecimal(totalAmount.toDouble())
        val roundedBigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP)

        return roundedBigDecimal.toFloat()
    }
    val investmentCategoryAndAmount: String
        get() {
            val sb = StringBuilder()
            for ((key, value) in investmentTypeAndAmount) {
                val amount = String.format( NumberFormat.getInstance().format(value))
                sb.append(key).append(" : ").append(amount).append("\n")
            }
            return sb.toString()
        }

    val investmentName: ArrayList<String>
    get() {
        val investmentName: ArrayList<String> = ArrayList()
        for(investment in getInvestmentsList()){
            investmentName.add(investment.investmentName)
        }
        return investmentName
    }

    val investmentMedium:ArrayList<String>
    get(){
        val investmentMediumList: ArrayList<String> = ArrayList()
        for (investment in getInvestmentsList()){
            investmentMediumList.add(investment.investmentMedium)
        }
        return investmentMediumList
    }

    val investmentCategory: ArrayList<String>
        get() {
            val investmentCategory: ArrayList<String> = ArrayList()
            for ((key) in investmentTypeAndAmount) {
                investmentCategory.add(key)
            }
            return investmentCategory
        }

    fun getTableNameFromDatabaseHelper():String{
        return db!!.getTableName()
    }

    fun getDatabaseHelper():DatabaseHelper{
        return db!!
    }

    init {
        db = DatabaseHelper(context)
        InvestmentsList.addAll(db.allInvestments)
    }
}