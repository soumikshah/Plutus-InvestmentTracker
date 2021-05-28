package com.soumikshah.investmenttracker.view

import com.soumikshah.investmenttracker.database.model.Investment
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.soumikshah.investmenttracker.view.InvestmentDetailAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.soumikshah.investmenttracker.R
import java.util.ArrayList

class InvestmentDetailFragment internal constructor(investmentList: ArrayList<Investment>?) : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var investmentName: TextView? = null
    private var investmentList: List<Investment>? = ArrayList()
    private var mAdapter: InvestmentDetailAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_investment_detail, container, false)
        investmentName = view.findViewById(R.id.investment_category_name)
        recyclerView = view.findViewById(R.id.recycler_view)
        mAdapter = InvestmentDetailAdapter(activity, investmentList)
        if (investmentList != null && investmentList!!.size > 0) investmentName!!.text = investmentList!![0].investmentCategory

        recyclerView!!.setHasFixedSize(true)
        val manager = GridLayoutManager(activity, 2)
        recyclerView!!.layoutManager = manager
        recyclerView!!.adapter = mAdapter
        return view
    }
    init {
        this.investmentList = investmentList
    }
}