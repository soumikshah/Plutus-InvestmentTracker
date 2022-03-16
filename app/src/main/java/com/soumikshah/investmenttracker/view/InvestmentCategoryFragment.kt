package com.soumikshah.investmenttracker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import java.util.*

class InvestmentCategoryFragment internal constructor(investmentList: ArrayList<Investment>?) : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var investmentName: TextView? = null
    private var investmentList: ArrayList<Investment>? = ArrayList()
    private var mBoxAdapter: MainpageDetailBoxAdapter? = null
    private var backButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category_detail, container, false)
        investmentName = view.findViewById(R.id.investment_category_name)
        backButton = view.findViewById(R.id.backButton)
        recyclerView = view.findViewById(R.id.recycler_view)
        mBoxAdapter = MainpageDetailBoxAdapter(requireContext(), investmentList!!)
        if (investmentList != null && investmentList!!.isNotEmpty())
            investmentName!!.text = investmentList!![0].investmentCategory

        recyclerView!!.setHasFixedSize(true)
        val manager = GridLayoutManager(activity, 2)
        recyclerView!!.layoutManager = manager
        recyclerView!!.adapter = mBoxAdapter
        backButton!!.setOnClickListener {
            //BackPress
            activity?.onBackPressed()
        }
        return view
    }

    init {
        this.investmentList = investmentList
    }

}