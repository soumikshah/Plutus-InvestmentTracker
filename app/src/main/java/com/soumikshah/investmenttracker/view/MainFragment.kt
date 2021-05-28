package com.soumikshah.investmenttracker.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.InvestmentHelper
import java.util.*

class MainFragment : Fragment() {
    private val noInvestmentView: TextView? = null
    private var pieChart: PieChart? = null
    @JvmField
    var investmentHelper: InvestmentHelper? = null
    private var pieEntries: ArrayList<PieEntry>? = null
    private var pieDataSet: PieDataSet? = null
    private var pieData: PieData? = null
    private var investmentMap: HashMap<String, Int>? = null
    private var investmentCategories: MutableList<String> = ArrayList()
    private var graphFragment: GraphFragment? = null
    private var recyclerView: RecyclerView? = null
    var fragment: RelativeLayout? = null
    private var mAdapter: InvestmentCategoryAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.mainfragment, container, false)
        //noInvestmentView = view.findViewById(R.id.empty_investment_view);
        //private InvestmentAdapter mAdapter;
        val totalAmount = view.findViewById<TextView>(R.id.total_amount_invested)
        val otherInvestment = view.findViewById<TextView>(R.id.otherInvestment)
        pieChart = view.findViewById(R.id.pieChart_view)
        recyclerView = view.findViewById(R.id.recycler_view)
        fragment = view.findViewById(R.id.fragment)
        investmentHelper = InvestmentHelper(requireContext())
        totalAmount.text = String.format(resources.getString(R.string.rs) + "%,d", investmentHelper!!.investmentTotalAmount)
        otherInvestment.text = investmentHelper!!.investmentCategoryAndAmount
        graphFragment = GraphFragment()
        mAdapter = InvestmentCategoryAdapter(requireContext(), investmentHelper!!.getInvestmentsList(), investmentCategories)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = mAdapter
        investmentMap = investmentHelper!!.investmentTypeAndAmount
        if (investmentMap != null) {
            for (type in investmentMap!!.keys) {
                if (!investmentCategories.contains(type)) {
                    Log.d("Tracker", "Here: $type")
                    investmentCategories.add(type)
                }
            }
        }
        initPieChart()
        showPieChart()
        mAdapter!!.notifyDataSetChanged()
        return view
    }

    private fun initPieChart() {
        pieChart!!.setUsePercentValues(true)
        pieChart!!.description.isEnabled = false
        pieChart!!.isRotationEnabled = true
        pieChart!!.rotationAngle = 180f
        pieChart!!.isHighlightPerTapEnabled = true
        pieChart!!.animateY(1400, Easing.EasingOption.EaseInOutQuad)
        pieChart!!.isDrawHoleEnabled = false
        pieChart!!.setDrawCenterText(false)
        pieChart!!.legend.isEnabled = false
    }

    private fun showPieChart() {
        pieEntries = ArrayList()
        val label = ""
        //input data and fit data into pie chart entry
        for (type in investmentMap!!.keys) {
            Log.d("Tracker", "Type: $type")
            pieEntries!!.add(PieEntry(investmentMap!![type]!!.toFloat(), type))
        }
        pieDataSet = PieDataSet(pieEntries, label)
        pieDataSet!!.valueTextSize = 12f
        pieDataSet!!.colors = graphFragment!!.listOfColor
        pieData = PieData(pieDataSet)
        pieData!!.setDrawValues(true)
        pieDataSet!!.sliceSpace = 0.1f
        pieData!!.setValueFormatter(PercentFormatter())
        pieChart!!.data = pieData
        pieChart!!.invalidate()
    }
}