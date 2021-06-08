package com.soumikshah.investmenttracker.view

import android.content.Context
import com.soumikshah.investmenttracker.database.model.Investment
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.soumikshah.investmenttracker.R
import java.util.*

class InvestmentCardAdapter(
    private val context: Context,
    private val investments: ArrayList<Investment>
) : RecyclerView.Adapter<InvestmentCardAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.item_investment_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val investment = investments[position]
        holder.investmentName!!.text =
            String.format("Investment Name is : %s", investment.investmentName)
        holder.investmentAmount!!.text = String.format(
            Locale.ENGLISH,
            "Investment Amount is : %,d",
            investment.investmentAmount
        )
        holder.investmentMedium!!.text =
            String.format("Investment Medium is :%s", investment.investmentMedium)
        holder.investmentCategory!!.text =
            String.format("Investment Category is :%s", investment.investmentCategory)
        //holder.investmentDate.setText(investment.getInvestmentDate());
    }

    override fun getItemCount(): Int {
        return investments.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val investmentName: TextView? = itemView.findViewById(R.id.investmentName)
        val investmentAmount: TextView? = itemView.findViewById(R.id.investmentAmount)
        val investmentMedium: TextView? = itemView.findViewById(R.id.investmentMedium)
        val investmentCategory: TextView? = itemView.findViewById(R.id.investmentCategory)
        private val investmentDate: TextView = itemView.findViewById(R.id.investmentDate)
    }
}