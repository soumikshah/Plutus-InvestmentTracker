package com.soumikshah.investmenttracker.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class CategoryScrollingCardView(
    private val context: Context,
    private val investments: ArrayList<Investment>
) : RecyclerView.Adapter<CategoryScrollingCardView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.category_scrolling_cardview, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val investment = investments[position]

        val graphColors = context.resources.getIntArray(R.array.graph_colors)
        val map = (context as MainActivity).mainFragment!!.getLegendColorAndName()
        for((k,v) in map){
            if(k == investment.investmentCategory){
                holder.parentView.setBackgroundColor(graphColors.get(v))
            }
        }

        holder.investmentName!!.text =
            String.format("Name: %s", investment.investmentName)
        holder.investmentAmount!!.text = String.format(
            "Amount: "+NumberFormat.getInstance().format(investment.investmentAmount),
            Locale.ENGLISH
        )
        if(investment.investmentMedium.isEmpty()){
            holder.investmentMedium!!.visibility =GONE
        }else{
            holder.investmentMedium!!.visibility = VISIBLE
            holder.investmentMedium.text =
                String.format("Medium: %s", investment.investmentMedium)
        }

        if(investment.investmentCategory.isEmpty()){
            holder.investmentCategory!!.visibility = GONE
        }else{
            holder.investmentCategory!!.visibility = VISIBLE
            holder.investmentCategory.text =
                String.format("Category: %s", investment.investmentCategory)
        }

        if(investment.investmentDate.toString() != "0"){
            val formatter = SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
            holder.investmentDate.visibility = VISIBLE
            holder.investmentDate.text = String.format("Date Invested On: %s",formatter.format(investment.investmentDate))
        }else{
            holder.investmentDate.visibility = GONE
        }

        if(investment.investmentPercent.isNaN() || investment.investmentPercent.equals(0f)){
           holder.investmentInterest.visibility= GONE
        }else{
            holder.investmentInterest.visibility = VISIBLE
            holder.investmentInterest.text = String.format("Invested At %s%% Interest",investment.investmentPercent.toString())
        }
        if((investment.investmentMonth.toString().isNotBlank() || investment.investmentMonth.toString().isNotEmpty())&& investment.investmentMonth != 0){
            holder.investmentPeriod.visibility = VISIBLE
            holder.investmentPeriod.text = String.format("Invested For %s Months",investment.investmentMonth)
        }else{
            holder.investmentPeriod.visibility = GONE
        }

        if((investment.investmentNumberOfUnits.toString().isEmpty())){
            holder.investmentNumberOfUnits.visibility = GONE
        }else{
            holder.investmentNumberOfUnits.visibility = VISIBLE
            holder.investmentNumberOfUnits.text = String.format("Number Of Units Bought: %s",investment.investmentNumberOfUnits)
        }

        if(investment.investmentPricePerUnit.toString() == "0"){
           holder.investmentPricePerUnit.visibility = GONE
        }else{
            holder.investmentPricePerUnit.visibility = VISIBLE
            holder.investmentPricePerUnit.text = String.format("Price Per Unit: "+NumberFormat.getInstance().format(investment.investmentPricePerUnit))
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
        val investmentInterest: TextView = itemView.findViewById(R.id.investmentPercent)
        val investmentPeriod: TextView = itemView.findViewById(R.id.investmentPeriod)
        val investmentNumberOfUnits: TextView = itemView.findViewById(R.id.investmentNumberOfUnits)
        val investmentPricePerUnit: TextView = itemView.findViewById(R.id.investmentPricePerUnit)
        val parentView:LinearLayout = itemView.findViewById(R.id.parent_view)
    }
}