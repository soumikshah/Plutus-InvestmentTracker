package com.soumikshah.investmenttracker.view

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.investment_dialog.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    var coordinatorLayout: CoordinatorLayout? = null
        private set
    private var viewPager: ViewPager? = null
    private var bottomNavigationView: BottomNavigationView? = null
    @JvmField
    var mainFragment: MainFragment? = null
    var investmentDateInLong: Long = 0
    var datePickerDialog: DatePickerDialog? = null
    var interestToBeReceived = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setActionBar(toolbar)
        if (actionBar != null) {
            actionBar!!.setDisplayHomeAsUpEnabled(false)
            actionBar!!.hide()
        }
        mainFragment = MainFragment()
        viewPager = findViewById(R.id.viewpager)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        setupViewPager(viewPager)
        bottomNavigationView!!.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainPage -> viewPager!!.currentItem = 0
                R.id.settings -> viewPager!!.currentItem = 1
                /*R.id.graph -> viewPager!!.currentItem = 1*/
                else -> throw IllegalStateException("Unexpected value: " + item.itemId)
            }
            true
        }

        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        bottomNavigationView!!.menu.findItem(R.id.mainPage).isChecked = true
                    }
                    1 -> bottomNavigationView!!.menu.findItem(R.id.settings).isChecked = true
                    /*1 -> bottomNavigationView!!.menu.findItem(R.id.graph).isChecked = true
                    2 -> bottomNavigationView!!.menu.findItem(R.id.settings).isChecked = true*/
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { showInvestmentDialog(false, null, -1) }
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(mainFragment, "Mainpage")
        adapter.addFragment(SettingsFragment(), "Settings")
        /*adapter.addFragment(GraphFragment(), "Graph")*/
        adapter.also { viewPager!!.adapter = it }
    }

    internal class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment?, title: String) {
            mFragmentList.add(fragment!!)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    fun showInvestmentDialog(shouldUpdate: Boolean, investment: Investment?, position: Int) {
        val layoutInflaterAndroid = LayoutInflater.from(this@MainActivity)
        val view = layoutInflaterAndroid.inflate(R.layout.investment_dialog, null)
        val alertDialogBuilderUserInput = AlertDialog.Builder(this@MainActivity)
        alertDialogBuilderUserInput.setView(view)
        val inputInvestmentName:EditText = view.findViewById<EditText>(R.id.investment)
        val inputInvestmentAmount: EditText = view.findViewById<EditText>(R.id.investmentAmount)
        val inputInvestmentPercent: EditText = view.findViewById<EditText>(R.id.investmentInterest)
        val inputInvestmentMedium: EditText = view.findViewById<EditText>(R.id.investmentMedium)
        val inputInvestmentCategory: EditText = view.findViewById<EditText>(R.id.investmentCategory)
        val inputInvestmentDate = view.findViewById<TextView>(R.id.investedDate)
        val inputInvestmentNumberOfMonths: EditText = view.findViewById<EditText>(R.id.investedNumberOfMonths)
        val dialogTitle = view.findViewById<TextView>(R.id.dialog_title)
        dialogTitle.text = if (!shouldUpdate) getString(R.string.new_investment_title) else getString(R.string.edit_investment_title)
        inputInvestmentDate.setOnClickListener { // calender class's instance and get current date , month and year from calender
            val c = Calendar.getInstance()
            val mYear = c[Calendar.YEAR] // current year
            val mMonth = c[Calendar.MONTH] // current month
            val mDay = c[Calendar.DAY_OF_MONTH] // current day
            // date picker dialog
            datePickerDialog = DatePickerDialog(this@MainActivity, R.style.DatePickerTheme,
                    { view, year, monthOfYear, dayOfMonth -> // set day of month , month and year value in the edit text
                        inputInvestmentDate.text = String.format(Locale.ENGLISH, "%d/%d/%d", dayOfMonth, monthOfYear + 1, year)
                        investmentDateInLong = c.timeInMillis
                    }, mYear, mMonth, mDay)
            datePickerDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.DKGRAY))
            datePickerDialog!!.show()
        }
        if (shouldUpdate && investment != null) {
            inputInvestmentName.setText(investment.investmentName.toString())
            inputInvestmentAmount.setText(investment.investmentAmount.toString())
            inputInvestmentPercent.setText(investment.investmentPercent.toString())
            inputInvestmentMedium.setText(investment.investmentMedium.toString())
            inputInvestmentCategory.setText(investment.investmentCategory.toString())
            if(inputInvestmentDate.text.isNotBlank()){
                val sim = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                inputInvestmentDate.text = sim.format(investment.investmentDate)
            }
            inputInvestmentNumberOfMonths.setText(investment.investmentMonth.toString())
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(if (shouldUpdate) "update" else "save") { _, _ -> }
                .setNegativeButton("cancel"
                ) { dialogBox, _ -> dialogBox.cancel() }
        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK) {
                alertDialog.dismiss()
            }
            false
        }
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener { // Show toast message when no text is entered
            if (TextUtils.isEmpty(inputInvestmentName.text.toString())) {
                Toast.makeText(this@MainActivity, "Enter Investment Name!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else if (TextUtils.isEmpty(inputInvestmentAmount.text.toString())) {
                Toast.makeText(this@MainActivity, "Enter Investment Amount!", Toast.LENGTH_LONG).show()
                return@OnClickListener
            } else if (TextUtils.isEmpty(inputInvestmentCategory.text.toString())) {
                Toast.makeText(this@MainActivity, "Enter Investment Type!", Toast.LENGTH_LONG).show()
            } else {
                alertDialog.dismiss()
            }
            interestToBeReceived = if (inputInvestmentPercent.text.toString().matches("".toRegex())) {
                0f
            } else {
                inputInvestmentPercent.text.toString().toFloat()
            }
            if (inputInvestmentMedium.text.toString().isEmpty()) {
                inputInvestmentMedium.setText(R.string.not_mentioned)
            }
            if (inputInvestmentCategory.text.toString().isEmpty()) {
                inputInvestmentCategory.setText("")
            }
            if (inputInvestmentNumberOfMonths.text.toString().isEmpty()) {
                inputInvestmentNumberOfMonths.setText("0")
            }
            if (shouldUpdate && investment != null) {
                mainFragment!!.investmentHelper!!.updateInvestment(inputInvestmentName.text.toString(), inputInvestmentAmount.text.toString().toInt(),
                        interestToBeReceived,
                        inputInvestmentMedium.text.toString(),
                        inputInvestmentCategory.text.toString(),
                        investmentDateInLong, inputInvestmentNumberOfMonths.text.toString().toInt(),
                        position)
            } else {
                mainFragment!!.investmentHelper!!.createInvestment(inputInvestmentName.text.toString(), inputInvestmentAmount.text.toString().toInt(),
                        interestToBeReceived,
                        inputInvestmentMedium.text.toString(),
                        inputInvestmentCategory.text.toString(),
                        investmentDateInLong, inputInvestmentNumberOfMonths.text.toString().toInt())
            }
            viewPager!!.adapter!!.notifyDataSetChanged()
        })
    }
}