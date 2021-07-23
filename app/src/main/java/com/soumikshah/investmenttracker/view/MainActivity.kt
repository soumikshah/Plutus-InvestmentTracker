package com.soumikshah.investmenttracker.view

import android.os.Bundle
import android.view.View
import android.widget.Toolbar
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
import java.util.*


class MainActivity : AppCompatActivity() {
    var coordinatorLayout: CoordinatorLayout? = null
        private set
    private var viewPager: ViewPager? = null
    private var bottomNavigationView: BottomNavigationView? = null
    @JvmField
    var mainFragment: MainFragment? = null
    private var fab: FloatingActionButton? =null
    private var adapter:ViewPagerAdapter? = null

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
                R.id.settings -> {
                    viewPager!!.currentItem = 1
                }
                /*R.id.graph -> viewPager!!.currentItem = 1*/
                else -> {
                    throw IllegalStateException("Unexpected value: " + item.itemId)
                }
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
                        viewPager!!.adapter!!.notifyDataSetChanged()
                    }
                    1 -> {
                        bottomNavigationView!!.menu.findItem(R.id.settings).isChecked = true
                        viewPager!!.adapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab!!.setOnClickListener {
            loadFragment(ShowDialog(false, null, -1))
        }
    }

    fun showFab(){
        fab!!.show()
    }
    fun hideFab(){
        fab!!.hide()
    }

    fun loadFragment(someFragment: Fragment){
        val fragment: Fragment = someFragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment, fragment) // give your fragment container id in first parameter
        transaction.addToBackStack(null) // if written, this transaction will be added to backstack
        transaction.commit()
    }

    fun updateViewPager(){
        if(adapter!=null){
            adapter!!.notifyDataSetChanged()
        }
    }
    private fun setupViewPager(viewPager: ViewPager?) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter!!.addFragment(mainFragment, getString(R.string.mainpage_viewpager_name))
        adapter!!.addFragment(SettingsFragment(), getString(R.string.setttings_viewpager_name))
        adapter.also { viewPager!!.adapter = it }
    }

    class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!) {
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
}