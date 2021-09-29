package com.soumikshah.investmenttracker.view

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.view.widget.TotalAmountInvestedWidget
import java.util.*
import java.util.concurrent.Executor


class MainActivity : AppCompatActivity() {
    var coordinatorLayout: CoordinatorLayout? = null
        private set
    private var viewPager: ViewPager? = null
    private var bottomNavigationView: BottomNavigationView? = null
    @JvmField
    var mainFragment: MainFragment? = null
    private var fab: FloatingActionButton? =null
    private var adapter:ViewPagerAdapter? = null

    override fun onDestroy() {
        super.onDestroy()
        //Update the widget when application is closed, so any changes made will be reflected in the widget right away.
        val intent = Intent(this,TotalAmountInvestedWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(application)
            .getAppWidgetIds(ComponentName(applicationContext,
                TotalAmountInvestedWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        fab = findViewById<View>(R.id.fab) as FloatingActionButton
        val executor = ContextCompat.getMainExecutor(this)
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
            {
                coordinatorLayout!!.visibility = GONE
                authUser(executor)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Toast.makeText(
                    this,
                    getString(R.string.error_msg_no_biometric_hardware),
                    Toast.LENGTH_LONG
                ).show()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(
                    this,
                    getString(R.string.error_msg_biometric_hw_unavailable),
                    Toast.LENGTH_LONG
                ).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Toast.makeText(
                    this,
                    getString(R.string.error_msg_biometric_not_setup),
                    Toast.LENGTH_LONG
                ).show()
        }

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

    override fun onBackPressed() {
        super.onBackPressed()
        val currentFragment =
            this.supportFragmentManager.findFragmentById(R.id.mainPage)
    }
    fun loadFragment(someFragment: Fragment){
        val fragment: Fragment = someFragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,
            R.anim.slide_in_left,R.anim.slide_out_right)
        transaction.add(R.id.fragment, fragment)
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

    private fun authUser(executor: Executor) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.auth_title))
            .setSubtitle(getString(R.string.auth_subtitle))
            .setDescription(getString(R.string.auth_description))
            .setDeviceCredentialAllowed(true)
            .build()

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    coordinatorLayout!!.visibility = View.VISIBLE
                }
                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        errString.toString(),
                        Toast.LENGTH_SHORT).show()
                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext,
                        getString(R.string.error_msg_auth_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        biometricPrompt.authenticate(promptInfo)
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