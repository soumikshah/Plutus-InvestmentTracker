package com.soumikshah.investmenttracker.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import com.soumikshah.investmenttracker.utils.RecyclerTouchListener
import kotlin.collections.ArrayList

class MainPageHorizontalAdapter internal constructor(private val context: Context, private var investmentList:
ArrayList<Investment>, private val investmentCategory: List<String>) : RecyclerView.Adapter<MainPageHorizontalAdapter.MyViewHolder>() {
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
            if (i.investmentCategory == investmentCat && i.investmentCategory.isNotEmpty()) {
                investmentData.add(i)
            }
        }
        val investmentHorizontalAdapter = CategorySwipingCardviewAdapter(context, investmentData)
        holder.horizontalView.setHasFixedSize(true)
        holder.horizontalView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.horizontalView.adapter = investmentHorizontalAdapter
        holder.investmentCategory.setOnClickListener {
            //(context as MainActivity).loadFragment(InvestmentCategoryFragment(investmentData))
        }
        holder.horizontalView.addOnItemTouchListener(
            RecyclerTouchListener(context, holder.horizontalView, object: AdapterView.OnItemClickListener,
                RecyclerTouchListener.ClickListener {
                override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                }

                override fun onClick(view: View?, position: Int) {
                    val categoryList: ArrayList<Investment> = ArrayList()
                    for((_,value ) in investmentList.withIndex()){
                        if(value.investmentCategory.equals(investmentData[position].investmentCategory)){
                            categoryList.add(value)
                        }
                    }
                    (context as MainActivity).loadFragment( DiscreteScrollviewDetailsFragment(categoryList,investmentData[position].investmentCategory,investmentData[position].id))
                }

                override fun onLongClick(view: View?, position: Int) {
                    (context as MainActivity).loadFragment(ShowDialogFragment(true,investmentData[position],position))
                }
            })
        )
    }

    override fun getItemCount(): Int {
        return investmentList.size
    }

    fun updateData(investmentListsCurrency: ArrayList<Investment>){
        investmentList.clear()
        investmentList.addAll(investmentListsCurrency)
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var investmentCategory: TextView = itemView.findViewById(R.id.category_of_the_investment)
        var horizontalView: RecyclerView = itemView.findViewById(R.id.horizontal_view)

    }
}