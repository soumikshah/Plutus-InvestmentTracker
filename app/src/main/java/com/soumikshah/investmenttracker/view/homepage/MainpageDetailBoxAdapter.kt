package com.soumikshah.investmenttracker.view.homepage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import com.soumikshah.investmenttracker.view.MainActivity
import com.soumikshah.investmenttracker.view.discretescrollview.DiscreteScrollviewDetailsFragment
import java.text.NumberFormat
import java.util.*

//Not called anywhere right now
class MainpageDetailBoxAdapter internal constructor(private val context: Context, investmentList: ArrayList<Investment>) : RecyclerView.Adapter<MainpageDetailBoxAdapter.MyViewHolder>() {
    private var investmentList: ArrayList<Investment> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_category_data_box, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val investment = investmentList[position]
        holder.investmentName.text = investment.investmentName
        holder.investmentMedium.text = investment.investmentMedium
        holder.investmentAmount.text = String.format(NumberFormat.getInstance().format(investment.investmentAmount))
        //todo holder.parent && moredetails will open new fragment with details about clicked investment.
        holder.investmentParent.setOnClickListener {
            (context as MainActivity).loadFragment(DiscreteScrollviewDetailsFragment(investmentList,investment.investmentCategory,investment.id))
        }
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
        var cardView:CardView = itemView.findViewById(R.id.card)
    }

    init {
        this.investmentList = investmentList
    }
}