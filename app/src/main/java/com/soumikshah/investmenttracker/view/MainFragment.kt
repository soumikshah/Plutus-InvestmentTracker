package com.soumikshah.investmenttracker.view

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
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
import com.soumikshah.investmenttracker.database.model.Investment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {
    private var noInvestmentView: TextView? = null
    private var pieChart: PieChart? = null
    @JvmField
    var investmentHelper: InvestmentHelper? = null
    private var pieEntries: ArrayList<PieEntry>? = null
    private var pieDataSet: PieDataSet? = null
    private var pieData: PieData? = null
    private var linearLayoutView: LinearLayout? = null
    private var investmentMap: HashMap<String, Int>? = null
    private var investmentCategories: MutableList<String> = ArrayList()
    private var graphFragment: GraphFragment? = null
    private var recyclerView: RecyclerView? = null
    private var currencyTextView: TextView? = null
    var fragment: RelativeLayout? = null
    private var mAdapter: MainPageHorizontalRecyclerview? = null
    private var totalAmount:TextView? = null
    private var otherInvestment:TextView? = null
    private var investmentListDemo: ArrayList<Investment>  = ArrayList()
    private var dollarInvestmentExists: Boolean = false

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideFab()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.mainfragment, container, false)
        noInvestmentView = view.findViewById(R.id.empty_investment_view)
        //private InvestmentAdapter mAdapter;
        totalAmount = view.findViewById(R.id.total_amount_invested)
        otherInvestment = view.findViewById(R.id.otherInvestment)
        val buttonGroup = view.findViewById<ThemedToggleButtonGroup>(R.id.toggleGroup)
        val inrButton = view.findViewById<ThemedButton>(R.id.rupee)
        val dollarButton = view.findViewById<ThemedButton>(R.id.dollar)
        pieChart = view.findViewById(R.id.pieChart_view)
        recyclerView = view.findViewById(R.id.recycler_view)
        fragment = view.findViewById(R.id.fragment)
        linearLayoutView = view.findViewById(R.id.linearLayout)
        investmentHelper = InvestmentHelper(requireContext())
        currencyTextView = view.findViewById(R.id.currency)
        graphFragment = GraphFragment()
        if(getCurrency()!!.isEmpty()){
            setCurrency(getString(R.string.inr))
        }
        if(investmentHelper!!.getInvestmentsList().isEmpty()){
            noInvestmentView!!.visibility = VISIBLE
            buttonGroup!!.visibility = GONE
            linearLayoutView!!.visibility = GONE
            pieChart!!.visibility = GONE
            recyclerView!!.visibility = GONE
        }else{
            noInvestmentView!!.visibility = GONE
            linearLayoutView!!.visibility = VISIBLE
            pieChart!!.visibility = VISIBLE
            recyclerView!!.visibility = VISIBLE

            for(investment in investmentHelper!!.getInvestmentsList()){
                if(investment.investmentCurrency.equals(getString(R.string.usd))){
                    dollarInvestmentExists = true
                    break
                }
            }
            if(!dollarInvestmentExists){
                buttonGroup.visibility = GONE
                currencyTextView!!.visibility = GONE
            }else{
                buttonGroup.visibility = VISIBLE
                currencyTextView!!.visibility = VISIBLE
            }
            investmentListDemo.addAll(investmentHelper!!.getInvestmentsListAccTOCurrency(getCurrency()!!))
            mAdapter = MainPageHorizontalRecyclerview(requireContext(),investmentListDemo , investmentCategories)
            val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.layoutManager = mLayoutManager
            //recyclerView!!.itemAnimator = DefaultItemAnimator()
            recyclerView!!.scheduleLayoutAnimation()
            recyclerView!!.adapter = mAdapter


            if(getCurrency().equals(getString(R.string.inr))){
                buttonGroup.selectButton(inrButton)
                loadData(getCurrency()!!)
            }else if(getCurrency().equals(getString(R.string.usd))){
                buttonGroup.selectButton(dollarButton)
                loadData(getCurrency()!!)
            }

            investmentMap = investmentHelper!!.investmentTypeAndAmount
            if (investmentMap != null) {
                for (type in investmentMap!!.keys) {
                    if (!investmentCategories.contains(type)) {
                        investmentCategories.add(type)
                    }
                }
            }
            mAdapter!!.notifyDataSetChanged()
            inrButton.setOnClickListener {
                setCurrency(getString(R.string.inr))
                loadData(getCurrency()!!)
            }
            dollarButton.setOnClickListener {
                setCurrency(getString(R.string.usd))
                loadData(getCurrency()!!)
            }
        }
        return view
    }
    private fun setCurrency(currencyName:String){
        val pref = requireContext().getSharedPreferences("currency_name", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("currency",currencyName)
        editor.apply()
    }

    private fun getCurrency(): String? {
        val pref = requireContext().getSharedPreferences("currency_name", Context.MODE_PRIVATE)
        return pref.getString("currency", "")
    }
    fun loadData(localCurrency: String){
        var currencyInString: String? = null
        if(getCurrency().equals(getString(R.string.inr))){
            currencyInString = getString(R.string.rs)
        }else if (getCurrency().equals(getString(R.string.usd))){
            currencyInString = getString(R.string.dollarSign)
        }
        totalAmount!!.text = String.format(currencyInString
                + "%,d", investmentHelper!!.investmentTotalAmountWithCurrency(localCurrency))
        otherInvestment!!.text = investmentHelper!!.investmentCategoryAndAmount
        if(recyclerView!=null && recyclerView!!.adapter!= null && recyclerView!!.adapter!!.itemCount>0){
            recyclerView!!.adapter!!.notifyDataSetChanged()
        }
        investmentCategories.clear()
        investmentMap = investmentHelper!!.investmentTypeAndAmount
        if (investmentMap != null) {
            for (type in investmentMap!!.keys) {
                if (!investmentCategories.contains(type)) {
                    investmentCategories.add(type)
                }
            }
        }
        investmentListDemo.clear()
        investmentListDemo.addAll(investmentHelper!!.getInvestmentsListAccTOCurrency(getCurrency()!!))
        initPieChart()
        showPieChart()
        if(mAdapter!= null){
            mAdapter!!.notifyDataSetChanged()
        }
    }
    private fun initPieChart() {
        pieChart!!.setUsePercentValues(true)
        pieChart!!.description.isEnabled = false
        pieChart!!.isRotationEnabled = true
        pieChart!!.rotationAngle = 180f
        pieChart!!.isHighlightPerTapEnabled = true
        pieChart!!.animateY(1400, Easing.EasingOption.EaseInOutQuad)
        pieChart!!.isDrawHoleEnabled = true
        pieChart!!.setHoleColor(android.R.color.transparent)
        pieChart!!.transparentCircleRadius = 10f
        pieChart!!.setDrawCenterText(true)
        pieChart!!.setDrawEntryLabels(false)
        pieChart!!.legend.isEnabled = true
        pieChart!!.legend.textSize = 15f
        pieChart!!.legend.isWordWrapEnabled = true

    }

    private fun showPieChart() {
        pieEntries = ArrayList()
        val label = ""
        //input data and fit data into pie chart entry
        for (type in investmentMap!!.keys) {
            pieEntries!!.add(PieEntry(investmentMap!![type]!!.toFloat(), type))
        }
        pieDataSet = PieDataSet(pieEntries, label)
        pieDataSet!!.valueTextSize = 12f
        pieDataSet!!.colors = graphFragment!!.listOfColor
        pieData = PieData(pieDataSet)
        pieData!!.setDrawValues(true)
        pieDataSet!!.sliceSpace = 0.1f
        pieData!!.setValueFormatter(PercentFormatter())
        pieData!!.setValueTextColor(Color.GRAY)
        pieData!!.setValueTextSize(14f)
        pieChart!!.data = pieData
        pieChart!!.invalidate()
    }
}