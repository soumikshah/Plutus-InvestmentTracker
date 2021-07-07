package com.soumikshah.investmenttracker.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import com.soumikshah.investmenttracker.utils.RecyclerTouchListener
import java.util.*

class MainPageHorizontalRecyclerview internal constructor(private val context: Context, private val investmentList: List<Investment>, private val investmentCategory: List<String>) : RecyclerView.Adapter<MainPageHorizontalRecyclerview.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.mainpage_horizontal_recyclerview, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (investmentCategory.size <= position) return
        val investmentCat = investmentCategory[position]
        holder.investmentCategory.text = String.format("%s " + context.resources.getString(R.string.arrow), investmentCat)
        holder.setIsRecyclable(false)
        val investmentData = ArrayList<Investment>()
        for (i in investmentList) {
            if (i.investmentCategory == investmentCat) {
                investmentData.add(i)
            }
        }
        val investmentHorizontalAdapter = CategorySwipingCardview(context, investmentData)
        holder.horizontalView.setHasFixedSize(true)
        holder.horizontalView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.horizontalView.adapter = investmentHorizontalAdapter
        holder.investmentCategory.setOnClickListener {
            val someFragment: Fragment = InvestmentCategoryFragment(investmentData)
            val transaction = (context as MainActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment, someFragment) // give your fragment container id in first parameter
            transaction.addToBackStack(null) // if written, this transaction will be added to backstack
            transaction.commit()
        }
        holder.horizontalView.addOnItemTouchListener(
            RecyclerTouchListener(context, holder.horizontalView, object: AdapterView.OnItemClickListener,
                RecyclerTouchListener.ClickListener {
                override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                }

                override fun onClick(view: View?, position: Int) {
                    //Todo open dialogbox showing item that is clicked
                    Log.d("Tracker", "Position $position")
                }

                override fun onLongClick(view: View?, position: Int) {
                }
            })
        )
    }

    override fun getItemCount(): Int {
        return investmentList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var investmentCategory: TextView = itemView.findViewById(R.id.category_of_the_investment)
        var horizontalView: RecyclerView = itemView.findViewById(R.id.horizontal_view)

    }
}