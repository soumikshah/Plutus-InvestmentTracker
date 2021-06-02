package com.soumikshah.investmenttracker.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import java.util.*

class InvestmentDetailAdapter internal constructor(private val context: Context, investmentList: List<Investment>) : RecyclerView.Adapter<InvestmentDetailAdapter.MyViewHolder>() {
    private var investmentList: List<Investment> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_investment_page, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val investment = investmentList[position]
        holder.investmentName.text = investment.investmentName
        holder.investmentMedium.text = investment.investmentMedium
        holder.investmentAmount.text = String.format(context.resources.getString(R.string.rs) + " %,d", investment.investmentAmount)
        //todo holder.parent && moredetails will open new fragment with details about clicked investment.
    }

    override fun getItemCount(): Int {
        return investmentList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var investmentName: TextView = itemView.findViewById(R.id.investment_name)
        var investmentMedium: TextView = itemView.findViewById(R.id.investmentMedium)
        var investmentAmount: TextView = itemView.findViewById(R.id.investmentAmount)
        var investmentMoreDetails: TextView = itemView.findViewById(R.id.view_more)
        var investmentParent: RelativeLayout = itemView.findViewById(R.id.parent)

    }

    init {
        this.investmentList = investmentList
    }
}