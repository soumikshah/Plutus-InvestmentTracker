package com.soumikshah.investmenttracker.view.homepage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import com.soumikshah.investmenttracker.view.MainActivity
import java.text.NumberFormat

//This adapter is used to populate categories on the homepage.
class CategorySwipingCardviewAdapter internal constructor(private val context: Context, private val investmentData: List<Investment>)
    : RecyclerView.Adapter<CategorySwipingCardviewAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.investmentName)
        var placeInvestmentDone: TextView = view.findViewById(R.id.investmentPlace)
        var amount: TextView = view.findViewById(R.id.amount)
        var parentView:LinearLayout = view.findViewById(R.id.parent_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_category_swiping_cardview, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val invest = investmentData[position]
        holder.name.text = invest.investmentName
        holder.placeInvestmentDone.text = invest.investmentMedium
        holder.amount.text = String.format(NumberFormat.getInstance().format(invest.investmentAmount))
        val graphColors = context.resources.getIntArray(R.array.graph_colors)
        val map = (context as MainActivity).mainFragment!!.getLegendColorAndName()
        for((k,v) in map){
            if(k == invest.investmentCategory){
                holder.parentView.setBackgroundColor(graphColors.get(v))
            }
        }
    }

    override fun getItemCount(): Int {
        return investmentData.size
    }
}