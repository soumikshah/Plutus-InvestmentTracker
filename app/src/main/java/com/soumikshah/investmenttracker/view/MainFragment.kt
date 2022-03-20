package com.soumikshah.investmenttracker.view

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.mynameismidori.currencypicker.ExtendedCurrency
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.InvestmentHelper
import com.soumikshah.investmenttracker.database.model.Investment
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup
import java.text.NumberFormat


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
    var fragment: RelativeLayout? = null
    private var mAdapter: MainPageHorizontalAdapter? = null
    private var totalAmount: TickerView? = null
    private var otherInvestment: TextView? = null
    private var investmentListDemo: ArrayList<Investment> = ArrayList()
    private var secondInvestmentExists: Boolean = false
    private var firstInvestmentExists: Boolean = false
    private var firstCurrency: String? = null
    private var secondCurrency: String? = null
    private var firstCurrencySymbol: String? = null
    private var secondCurrencySymbol: String? = null
    private var pref: SharedPreferences? = null

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideFab()
    }

    override fun onDestroy() {
        super.onDestroy()
        investmentHelper!!.getDatabaseHelper().close()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        graphFragment = GraphFragment()
        totalAmount!!.setCharacterLists(TickerUtils.provideNumberList())
        totalAmount!!.animationInterpolator = OvershootInterpolator()
        totalAmount!!.gravity = Gravity.START
        pref = requireContext().getSharedPreferences(
            "currency_name",
            android.content.Context.MODE_PRIVATE
        )
        if (investmentHelper!!.getInvestmentsList().isEmpty()) {
            buttonGroup!!.visibility = GONE
            linearLayoutView!!.visibility = GONE
            pieChart!!.visibility = GONE
            recyclerView!!.visibility = GONE
            if (!getCurrency().isNullOrEmpty()) {
                noInvestmentView!!.visibility = VISIBLE
            } else {
                noInvestmentView!!.visibility = GONE
                Log.d("Plutus","Here?")
                (activity as MainActivity).replaceFragment(AppIntroFragment())
            }
        } else {
            noInvestmentView!!.visibility = GONE
            linearLayoutView!!.visibility = VISIBLE
            pieChart!!.visibility = VISIBLE
            recyclerView!!.visibility = VISIBLE
            firstCurrency = getCurrency()
            secondCurrency = getCurrency2()
            if (getCurrencySymbol() != null && !getCurrencySymbol().equals("0")) {
                firstCurrencySymbol = getCurrencySymbol()
            } else {
                firstCurrencySymbol = fetchCurrencySymbol(firstCurrency!!)
            }
            if (getCurrencySymbol2() != null && !getCurrencySymbol2().equals("0")) {
                secondCurrencySymbol = getCurrencySymbol2()
            } else {
                secondCurrencySymbol = fetchCurrencySymbol(secondCurrency!!)
            }

            firstButton!!.text = firstCurrencySymbol.toString()
            if (secondCurrency.isNullOrEmpty()) {
                secondButton!!.visibility = GONE
            } else {
                secondButton!!.visibility = VISIBLE
                secondButton.text = secondCurrencySymbol.toString()
            }
            for (investment in investmentHelper!!.getInvestmentsList()) {
                if (investment.investmentCurrency.equals(secondCurrency)) {
                    secondInvestmentExists = true
                    break
                }
            }
            for (investment in investmentHelper!!.getInvestmentsList()) {
                if (investment.investmentCurrency.equals(firstCurrency)) {
                    firstInvestmentExists = true
                    break
                }
            }
            if (!secondInvestmentExists) {
                buttonGroup.visibility = GONE
            } else if (!firstInvestmentExists) {
                buttonGroup.visibility = GONE
            } else {
                buttonGroup.visibility = VISIBLE
            }
            investmentListDemo.addAll(investmentHelper!!.getInvestmentsListAccTOCurrency(getCurrency()!!))
            mAdapter = MainPageHorizontalAdapter(
                requireContext(),
                investmentListDemo,
                investmentCategories
            )
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

    /* private fun clearCurrency(){
         val pref = requireContext().getSharedPreferences("currency_name",Context.MODE_PRIVATE)
         val editor = pref.edit()
         editor.clear()
         editor.apply()
     }*/

    fun fetchCurrencySymbol(currencyCode: String): String? {
        val currencies: Array<ExtendedCurrency>? = ExtendedCurrency.CURRENCIES
        var currencySymbol: String? = null
        if (currencies != null) {
            for (currency in currencies) {
                if (currency.code == currencyCode) {
                    currencySymbol = if (currency.code.equals("INR")) {
                        Log.d("Plutus", "CurrencySymbol ${currency.code}")
                        "₹"
                    } else {
                        Log.d("Plutus", "CurrencySymbol ${currency.symbol}")
                        currency.symbol
                    }
                }
            }
        }
        if (currencySymbol != null && currencySymbol.equals("0")) {
            return currencyCode
        }
        return currencySymbol
    }

    private fun loadData(localCurrency: String) {
        var currencyInString: String? = null
        var currencySymbol: String? = null
        if (localCurrency == firstCurrency) {
            currencyInString = firstCurrency!!.toString()
            currencySymbol = fetchCurrencySymbol(currencyInString)
        } else if (localCurrency == secondCurrency) {
            currencyInString = secondCurrency!!.toString()
            currencySymbol = fetchCurrencySymbol(currencyInString)
        }
        totalAmount!!.text = String.format(
            "$currencySymbol"
                    + NumberFormat.getInstance().format(
                investmentHelper!!.investmentTotalAmountWithCurrency(localCurrency)
            )
        )
        //otherInvestment!!.text = investmentHelper!!.investmentCategoryAndAmount
        if (recyclerView != null && recyclerView!!.adapter != null && recyclerView!!.adapter!!.itemCount > 0) {
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
        investmentListDemo.addAll(
            investmentHelper!!.getInvestmentsListAccTOCurrency(
                currencyInString!!
            )
        )
        initPieChart()
        showPieChart()
        if (mAdapter != null) {
            mAdapter!!.notifyDataSetChanged()
        }
    }

    private fun initPieChart() {
        pieChart!!.setUsePercentValues(true)
        pieChart!!.description.isEnabled = false
        pieChart!!.rotationAngle = 180f
        pieChart!!.animateY(1400, Easing.EasingOption.EaseInOutQuad)
        pieChart!!.isDrawHoleEnabled = true
        pieChart!!.holeRadius = 45f
        pieChart!!.setHoleColor(android.R.color.transparent)
        pieChart!!.setDrawEntryLabels(false)
        pieChart!!.setExtraOffsets(25f, 0f, 25f, 0f)
        pieChart!!.setUsePercentValues(true);
        pieChart!!.dragDecelerationFrictionCoef = 0.95f;
        // enable rotation of the chart by touch
        pieChart!!.isHighlightPerTapEnabled = true;
        pieChart!!.highlightValues(null);
        /*new*/
        pieChart!!.setDrawCenterText(false);
        pieChart!!.transparentCircleRadius = 35f;

        pieChart!!.isRotationEnabled = false;

        pieChart!!.setEntryLabelColor(Color.WHITE);
        pieChart!!.setEntryLabelTextSize(11f);
        val l: Legend = pieChart!!.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.isWordWrapEnabled = true
        l.textSize = 12f
        l.setDrawInside(false)
        l.isEnabled = true

    }

    fun pieDatasetSlice(dataSet: PieDataSet): PieData {
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.valueLinePart1OffsetPercentage = 100f
        /** When valuePosition is OutsideSlice, indicates offset as percentage out of the slice size  */
        dataSet.valueLinePart1Length = 0.50f
        /** When valuePosition is OutsideSlice, indicates length of first half of the line  */
        dataSet.valueLinePart2Length = 0.3f
        /** When valuePosition is OutsideSlice, indicates length of second half of the line  */
        dataSet.sliceSpace = 3f
        val data = PieData(dataSet)
        data.setValueTextSize(14f)
        data.setValueTextColor(Color.BLACK)
        data.setValueFormatter(PercentFormatter())
        return data
    }

    private fun showPieChart() {
        pieEntries = ArrayList()
        val label = ""
        //input data and fit data into pie chart entry
        for (type in investmentMap!!.keys) {
            pieEntries!!.add(PieEntry(investmentMap!![type]!!.toFloat(), type))
        }

        pieDataSet = PieDataSet(pieEntries, label)
        pieDataSet!!.colors = getGraphColor()
        pieData = pieDatasetSlice(pieDataSet!!)
        pieChart!!.data = pieData
        pieChart!!.invalidate()
    }

    fun getLegendColorAndName(): HashMap<String, Int> {
        val legendEntries = pieChart!!.legend.entries

        val colorAndLabelName: HashMap<String, Int> = HashMap()

        var indexIncrementor = 0
        for (i in legendEntries.indices) {

            if (legendEntries[i].label.isNotEmpty() || legendEntries[i].label != null) {
                colorAndLabelName.put(legendEntries[i].label, indexIncrementor)
                indexIncrementor++
            }
        }
        return colorAndLabelName
    }

    fun loadEmptyViewFragment(someFragment: Fragment) {
        val transaction = activity?.supportFragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        transaction.replace(R.id.fragment, someFragment)
        transaction.commit()
    }

    fun getGraphColor(): ArrayList<Int> {
        val graphColor = java.util.ArrayList<Int>()
        val colorArray = resources.getIntArray(R.array.graph_colors)

        for (color in colorArray) {
            graphColor.add(color)
        }
        return graphColor
    }

    fun setCurrencySymbol(currencySymbol: String) {
        val editor = pref!!.edit()
        editor.putString("currencySymbol", currencySymbol)
        editor.apply()
    }

    fun setCurrencySymbol2(currencySymbol: String) {
        val editor = pref!!.edit()
        editor.putString("currency2Symbol", currencySymbol)
        editor.apply()
    }

    fun setCurrency2(currencyName: String) {
        val editor = pref!!.edit()
        editor.putString("currency2", currencyName)
        editor.apply()
    }

    fun setCurrency(currencyName: String) {
        val editor = pref!!.edit()
        editor.putString("currency", currencyName)
        editor.apply()
    }

    fun getCurrency(): String? {
        return pref!!.getString("currency", "")
    }

    fun getCurrency2(): String? {
        return pref!!.getString("currency2", "")
    }

    fun getCurrencySymbol(): String? {
        return pref!!.getString("currencySymbol", "")
    }

    fun getCurrencySymbol2(): String? {
        return pref!!.getString("currency2Symbol", "")
    }
}