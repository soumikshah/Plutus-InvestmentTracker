package com.soumikshah.investmenttracker.view.graph

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
import com.soumikshah.investmenttracker.view.MainActivity
import java.util.*

class GraphFragment : Fragment(), OnItemSelectedListener {
    private var pieChart: PieChart? = null
    private var pieData: PieData? = null
    private var investmentMap: HashMap<String, Float>? = null
    private var copyOfInvestmentMap = HashMap<String, Float>()
    private var investmentCategoryInAList: ArrayList<String>? = null
    private var investments: ArrayList<Investment>? = null
    private var pieEntries: ArrayList<PieEntry>? = null
    private var pieDataSet: PieDataSet? = null
    private var spinner: Spinner? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_graph, container, false)
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
        pieDataSet!!.colors = (activity as MainActivity).mainFragment!!.getGraphColor()
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
    }
    //initializing colors for the entries

    //Setting up colors for piechart
    val listOfColor: ArrayList<Int>
        get() {
            //initializing colors for the entries
            val colors = ArrayList<Int>()
            val colorArray = resources.getIntArray(R.array.graph_colors)

            for(color in colorArray){
                colors.add(color)
            }
/*            colors.add(Color.parseColor("#9ADCFF"))
            colors.add(Color.parseColor("#8C489F"))
            colors.add(Color.parseColor("#421C52"))
            colors.add(Color.parseColor("#C3C3E5"))
            colors.add(Color.parseColor("#F1F0FF"))
            colors.add(Color.parseColor("#732C7B"))
            colors.add(Color.parseColor("#9C8AA5"))
            colors.add(Color.parseColor("#BDAEC6"))
            colors.add(Color.parseColor("#6600CC"))
            colors.add(Color.parseColor("#FFCC00"))
            colors.add(Color.parseColor("#000000"))
            colors.add(Color.parseColor("#CC0000"))*/
            return colors
        }

    private fun refreshPieChart(pieData: PieData) {
        pieChart!!.data = pieData
        pieChart!!.invalidate()
    }
}