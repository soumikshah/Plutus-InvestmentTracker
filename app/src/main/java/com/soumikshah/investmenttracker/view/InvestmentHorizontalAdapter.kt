package com.soumikshah.investmenttracker.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment

class InvestmentHorizontalAdapter internal constructor(private val context: Context, private val investmentData: List<Investment>) : RecyclerView.Adapter<InvestmentHorizontalAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.investmentName)
        var placeInvestmentDone: TextView = view.findViewById(R.id.investmentPlace)
        var amount: TextView = view.findViewById(R.id.amount)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_swiping_cardview, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val invest = investmentData[position]
        holder.name.text = invest.investmentName
        holder.placeInvestmentDone.text = invest.investmentMedium
        holder.amount.text = String.format(context.resources.getString(R.string.rs) + "%,d", invest.investmentAmount)
    }

    override fun getItemCount(): Int {
        return investmentData.size
    }
}