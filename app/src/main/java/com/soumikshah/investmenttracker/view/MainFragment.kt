package com.soumikshah.investmenttracker.view

import android.content.SharedPreferences
import android.graphics.Color
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.InvestmentHelper
import com.soumikshah.investmenttracker.database.model.Investment
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup
import java.text.NumberFormat
import java.util.*


class MainFragment : Fragment() {
    private var noInvestmentView: TextView? = null
    private var pieChart: PieChart? = null
    @JvmField
    var investmentHelper: InvestmentHelper? = null
    private var pieEntries: ArrayList<PieEntry>? = null
    private var pieDataSet: PieDataSet? = null
    private var pieData: PieData? = null
    private var linearLayoutView: LinearLayout? = null
    private var investmentMap: HashMap<String, Float>? = null
    private var investmentCategories: MutableList<String> = ArrayList()
    private var graphFragment: GraphFragment? = null
    private var recyclerView: RecyclerView? = null
    private var currencyTextView: TextView? = null
    var fragment: RelativeLayout? = null
    private var mAdapter: MainPageHorizontalRecyclerview? = null
    private var totalAmount:TextView? = null
    private var otherInvestment:TextView? = null
    private var investmentListDemo: ArrayList<Investment>  = ArrayList()
    private var secondInvestmentExists: Boolean = false
    private var firstInvestmentExists: Boolean = false
    private var firstCurrency:String? = null
    private var secondCurrency:String? = null
    private var pref:SharedPreferences? = null

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideFab()
    }

    override fun onDestroy() {
        super.onDestroy()
        investmentHelper!!.getDatabaseHelper().close()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.mainfragment, container, false)
        noInvestmentView = view.findViewById(R.id.empty_investment_view)
        //private InvestmentAdapter mAdapter;
        totalAmount = view.findViewById(R.id.total_amount_invested)
        otherInvestment = view.findViewById(R.id.otherInvestment)
        val buttonGroup = view.findViewById<ThemedToggleButtonGroup>(R.id.toggleGroup)
        val firstButton = view.findViewById<ThemedButton>(R.id.firstcurrency)
        val secondButton = view.findViewById<ThemedButton>(R.id.secondcurrency)
        pieChart = view.findViewById(R.id.pieChart_view)
        recyclerView = view.findViewById(R.id.recycler_view)
        fragment = view.findViewById(R.id.fragment)
        linearLayoutView = view.findViewById(R.id.linearLayout)
        investmentHelper = InvestmentHelper(requireContext())
        currencyTextView = view.findViewById(R.id.currency)
        graphFragment = GraphFragment()
        pref = requireContext().getSharedPreferences("currency_name", android.content.Context.MODE_PRIVATE)
        if(investmentHelper!!.getInvestmentsList().isEmpty()){
            buttonGroup!!.visibility = GONE
            linearLayoutView!!.visibility = GONE
            pieChart!!.visibility = GONE
            recyclerView!!.visibility = GONE
            if(!getCurrency().isNullOrEmpty()){
                noInvestmentView!!.visibility = VISIBLE
            }else{
                noInvestmentView!!.visibility = GONE
                loadEmptyViewFragment(EmptyViewFragment())
            }
        }else{
            noInvestmentView!!.visibility = GONE
            linearLayoutView!!.visibility = VISIBLE
            pieChart!!.visibility = VISIBLE
            recyclerView!!.visibility = VISIBLE
            firstCurrency = getCurrency()
            secondCurrency = getCurrency2()


            firstButton!!.text = firstCurrency.toString()
            if(secondCurrency.isNullOrEmpty()){
                secondButton!!.visibility = GONE
            }else{
                secondButton!!.visibility = VISIBLE
                secondButton.text = secondCurrency.toString()
            }
            for(investment in investmentHelper!!.getInvestmentsList()){
                if(investment.investmentCurrency.equals(secondCurrency)){
                    secondInvestmentExists = true
                    break
                }
            }
            for(investment in investmentHelper!!.getInvestmentsList()){
                if(investment.investmentCurrency.equals(firstCurrency)){
                    firstInvestmentExists = true
                    break
                }
            }
            if(!secondInvestmentExists){
                buttonGroup.visibility = GONE
                currencyTextView!!.visibility = GONE
            }else if(!firstInvestmentExists){
                buttonGroup.visibility = GONE
                currencyTextView!!.visibility = GONE
            } else{
                buttonGroup.visibility = VISIBLE
                currencyTextView!!.visibility = VISIBLE
            }
            investmentListDemo.addAll(investmentHelper!!.getInvestmentsListAccTOCurrency(getCurrency()!!))
            mAdapter = MainPageHorizontalRecyclerview(requireContext(),investmentListDemo , investmentCategories)
            val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.layoutManager = mLayoutManager
            recyclerView!!.adapter = mAdapter
            recyclerView!!.scheduleLayoutAnimation()

            investmentMap = investmentHelper!!.investmentTypeAndAmount
            if (investmentMap != null) {
                for (type in investmentMap!!.keys) {
                    if (!investmentCategories.contains(type) && type.isNotEmpty()) {
                        investmentCategories.add(type)
                    }
                }
            }
            buttonGroup.selectButton(firstButton)
            loadData(getCurrency()!!)
            getLegendColorAndName()
            mAdapter!!.notifyDataSetChanged()
            firstButton.setOnClickListener {
                buttonGroup.selectButton(firstButton)
                loadData(getCurrency()!!)
            }
            secondButton.setOnClickListener {
                buttonGroup.selectButton(secondButton)
                loadData(getCurrency2()!!)
            }
        }
        return view
    }
    fun setCurrency2(currencyName: String){
        val editor = pref!!.edit()
        editor.putString("currency2",currencyName)
        editor.apply()
    }
    fun setCurrency(currencyName:String){
        val editor = pref!!.edit()
        editor.putString("currency",currencyName)
        editor.apply()
    }

   /* private fun clearCurrency(){
        val pref = requireContext().getSharedPreferences("currency_name",Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.clear()
        editor.apply()
    }*/

    fun getLegendColorAndName():HashMap<String,Int>{
        val legendEntries = pieChart!!.legend.entries

        val colorAndLabelName:HashMap<String,Int> = HashMap()

        var indexIncrementor = 0
        for(i in legendEntries.indices){

            if(legendEntries[i].label.isNotEmpty() || legendEntries[i].label!=null){
                colorAndLabelName.put(legendEntries[i].label, indexIncrementor)
                indexIncrementor++
            }
        }
        return colorAndLabelName
    }

    fun getCurrency(): String? {
        return pref!!.getString("currency", "")
    }

    fun getCurrency2(): String? {
        return pref!!.getString("currency2","")
    }

    private fun loadData(localCurrency: String){
        var currencyInString: String? = null
        if(localCurrency == firstCurrency){
            currencyInString = firstCurrency!!.toString()
        }else if (localCurrency == secondCurrency){
            currencyInString = secondCurrency!!.toString()
        }
        totalAmount!!.text = String.format(
            "$currencyInString "
                + NumberFormat.getInstance().format(investmentHelper!!.
        investmentTotalAmountWithCurrency(localCurrency)))
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
        investmentListDemo.addAll(investmentHelper!!.getInvestmentsListAccTOCurrency(currencyInString!!))
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

    private fun loadEmptyViewFragment(someFragment:Fragment){
        val transaction = activity?.supportFragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
        transaction.replace(R.id.fragment, someFragment)
        transaction.commit()
    }

    fun getGraphColor(): ArrayList<Int> {
        val graphColor = java.util.ArrayList<Int>()
        val colorArray = resources.getIntArray(R.array.graph_colors)

        for(color in colorArray){
            graphColor.add(color)
        }
        return graphColor
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
        pieDataSet!!.colors = getGraphColor()
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