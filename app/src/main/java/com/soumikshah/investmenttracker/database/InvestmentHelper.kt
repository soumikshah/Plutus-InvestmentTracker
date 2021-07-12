package com.soumikshah.investmenttracker.database

import android.content.Context
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import java.util.*

class InvestmentHelper(var context: Context) {
    fun getInvestmentsList(): ArrayList<Investment> {
        return InvestmentsList
    }

    private val InvestmentsList: ArrayList<Investment> = ArrayList()
    private val db: DatabaseHelper?
    private var nullDb = false
    val investmentTypeAndAmount: HashMap<String, Int> = HashMap()
    fun createInvestment(investmentName: String?,
                         investmentAmount: Int,
                         investmentPercent: Float,
                         investmentMedium: String?,
                         investmentCategory: String?,
                         investmentDate: Long,
                         investmentMonth: Int,
                         investmentNumberOfUnits:String,
                         investmentPricePerUnit:Int,
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

    fun updateInvestment(investment: String?, investmentAmount: Int,
                         investmentPercent: Float, investmentMedium: String?,
                         investmentCategory: String?,
                         investmentDate: Long,
                         investmentMonth: Int,
                         investmentNumberOfUnits: String,
                         investmentPricePerUnit: Int,
                         investmentCurrency: String?,
                         position: Int) {
        val n = InvestmentsList[position]
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

    private fun deleteInvestment(position: Int) {
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

    val investmentTotalAmount: Int
        get() {
            var totalAmount = 0
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
    val investmentCategoryAndAmount: String
        get() {
            val sb = StringBuilder()
            for ((key, value) in investmentTypeAndAmount!!) {
                val amount = String.format(context.resources.getString(R.string.rs) + "%,d", value)
                sb.append(key).append(" : ").append(amount).append("\n")
            }
            return sb.toString()
        }
    val investmentCategory: ArrayList<String>
        get() {
            val investmentCategory: ArrayList<String> = ArrayList()
            for ((key) in investmentTypeAndAmount!!) {
                investmentCategory.add(key)
            }
            return investmentCategory
        }

    init {
        db = DatabaseHelper(context)
        InvestmentsList.addAll(db.allInvestments)
    }
}