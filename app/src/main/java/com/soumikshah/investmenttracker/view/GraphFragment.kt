package com.soumikshah.investmenttracker.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import java.util.*

class GraphFragment : Fragment(), OnItemSelectedListener {
    private var pieChart: PieChart? = null
    private var pieData: PieData? = null
    private var investmentMap: HashMap<String, Int>? = null
    private var copyOfInvestmentMap = HashMap<String, Int>()
    private var investmentCategoryInAList: ArrayList<String>? = null
    private var investments: ArrayList<Investment>? = null
    private var pieEntries: ArrayList<PieEntry>? = null
    private var pieDataSet: PieDataSet? = null
    private var spinner: Spinner? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.graphfragment, container, false)
        pieChart = view.findViewById(R.id.pieChart_view)
        spinner = view.findViewById(R.id.spinner)
        if (activity != null) {
            investmentMap = (activity as MainActivity?)!!.mainFragment!!.investmentHelper!!.investmentTypeAndAmount
            investmentCategoryInAList = (activity as MainActivity?)!!.mainFragment!!.investmentHelper!!.investmentCategory
            investments = (activity as MainActivity?)!!.mainFragment!!.investmentHelper!!.getInvestmentsList()
            investmentCategoryInAList!!.add("All")
        }
        investmentCategoryInAList!!.sort()
        val dataAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, investmentCategoryInAList!!)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = dataAdapter
        spinner!!.onItemSelectedListener = this
        initPieChart()
        showPieChart()
        return view
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        val item = adapterView.getItemAtPosition(i).toString()
        if (item != "All") {
            getCategory(item)
        } else {
            allCategories
        }
        resetChart()
        showPieChart()
    }

    val allCategories: Unit
        get() {
            if (activity != null && investmentCategoryInAList != null) {
                if (investmentCategoryInAList!!.size >= 0 && !investmentMap!!.containsKey(investmentCategoryInAList!![0])) {
                    investmentMap!!.clear()
                    investmentMap!!.putAll(copyOfInvestmentMap)
                }
                copyOfInvestmentMap.putAll(investmentMap!!)
            }
        }

    fun getCategory(category: String) {
        emptyMap<Any, Any>()
        investmentMap!!.clear()
        for (i in investments!!.indices) {
            if (investments!![i].investmentCategory == category) {
                investmentMap!![investments!![i].investmentName] = investments!![i].investmentAmount
            }
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    private fun resetChart() {
        pieEntries!!.clear()
        if (pieData != null) {
            pieData = null
        }
        pieChart!!.data.clearValues()
        pieChart!!.clear()
        pieChart!!.invalidate()
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
        pieDataSet!!.colors = listOfColor
        pieData = PieData(pieDataSet)
        pieData!!.setDrawValues(true)
        pieData!!.setValueFormatter(PercentFormatter())
        refreshPieChart(pieData!!)
    }

    //Initialize piechart
    private fun initPieChart() {
        pieChart!!.setUsePercentValues(true)
        pieChart!!.description.isEnabled = false
        pieChart!!.isRotationEnabled = true
        pieChart!!.rotationAngle = 180f
        pieChart!!.isHighlightPerTapEnabled = true
        pieChart!!.animateY(1400, Easing.EasingOption.EaseInOutQuad)
        pieChart!!.setHoleColor(Color.parseColor("#000000"))
        val legend = pieChart!!.legend
        legend.isWordWrapEnabled = true
        legend.textSize = 16f
        legend.textColor = Color.WHITE
        legend.form = Legend.LegendForm.CIRCLE
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
    }//initializing colors for the entries

    //Setting up colors for piechart
    val listOfColor: ArrayList<Int>
        get() {
            //initializing colors for the entries
            val colors = ArrayList<Int>()
            colors.add(Color.parseColor("#304567"))
            colors.add(Color.parseColor("#309967"))
            colors.add(Color.parseColor("#476567"))
            colors.add(Color.parseColor("#890567"))
            colors.add(Color.parseColor("#a35567"))
            colors.add(Color.parseColor("#ff5f67"))
            colors.add(Color.parseColor("#3ca567"))
            return colors
        }

    private fun refreshPieChart(pieData: PieData) {
        pieChart!!.data = pieData
        pieChart!!.invalidate()
    }
}