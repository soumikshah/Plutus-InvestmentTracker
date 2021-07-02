package com.soumikshah.investmenttracker.view

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import java.text.SimpleDateFormat
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
            String.format("Name: %s", investment.investmentName)
        holder.investmentAmount!!.text = String.format(
            Locale.ENGLISH,
            "Amount: %,d",
            investment.investmentAmount
        )
        holder.investmentMedium!!.text =
            String.format("Medium: %s", investment.investmentMedium)
        holder.investmentCategory!!.text =
            String.format("Category: %s", investment.investmentCategory)
        if(investment.investmentDate.toString().isNotEmpty()){
            val formatter = SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
            holder.investmentDate.visibility = VISIBLE
            holder.investmentDate.text = String.format("Date Invested On: %s",formatter.format(investment.investmentDate))
        }else{
            holder.investmentDate.visibility = GONE
        }
    }

    override fun getItemCount(): Int {
        return investments.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val investmentName: TextView? = itemView.findViewById(R.id.investmentName)
        val investmentAmount: TextView? = itemView.findViewById(R.id.investmentAmount)
        val investmentMedium: TextView? = itemView.findViewById(R.id.investmentMedium)
        val investmentCategory: TextView? = itemView.findViewById(R.id.investmentCategory)
        val investmentDate: TextView = itemView.findViewById(R.id.investmentDate)
    }
}