package com.soumikshah.investmenttracker.view.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.ajts.androidmads.library.SQLiteToExcel
import com.ajts.androidmads.library.SQLiteToExcel.ExportListener
import com.mynameismidori.currencypicker.CurrencyPicker
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.view.MainActivity
import com.soumikshah.investmenttracker.view.aboutus.AboutUs
import java.io.File


class SettingsFragment internal constructor() : Fragment() {
    private var sampleTextView: TextView? = null
    private var exportButton: Button? = null
    private var importButton: Button? = null
    private var secondCurrencyButton: Button? = null
    private var aboutUsButton: Button? = null
    private val fileProvider = "com.soumikshah.investmenttracker.provider"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        sampleTextView = view.findViewById(R.id.sampleTextView)
        exportButton = view.findViewById(R.id.exportButton)
        importButton = view.findViewById(R.id.importButton)
        aboutUsButton = view.findViewById(R.id.aboutUsButton)
        secondCurrencyButton = view.findViewById(R.id.enableSecondCurrencyButton)
        sampleTextView!!.text = getString(R.string.sampletext)
        sampleTextView!!.visibility = GONE
        val secondCurrencyValue: String? = (context as MainActivity).mainFragment!!.getCurrency2()
        val firstCurrencyValue: String? = (context as MainActivity).mainFragment!!.getCurrency()
        if (firstCurrencyValue.isNullOrEmpty() || !secondCurrencyValue.isNullOrEmpty()) {
            secondCurrencyButton!!.visibility = GONE
        }
        val dbName: String =
            (activity as MainActivity).mainFragment!!.investmentHelper!!.getTableNameFromDatabaseHelper()
        val directoryPath = createDirectoryPath("InvestmentTracker")!!.absolutePath
        //Hide export button if DB is empty.
        if ((activity as MainActivity).mainFragment!!.investmentHelper!!.getInvestmentsList()
                .isEmpty()
        ) {
            exportButton!!.visibility = GONE
        }
        exportButton!!.setOnClickListener {
            //Check for permission only when user clicks on export button.
            isStoragePermissionGranted()

            //Implemented sleep so it gives user some time to accept or deny storage permissions.
            try {
                Thread.sleep(2000)
            } catch (exception: Exception) {
                exception.printStackTrace()
                Log.e("InvestmentTracker", "${exception.message}")
            }
            //Converting to excel and then calling send intent when converting is completed.
            convertToExcel(dbName, directoryPath)
        }

        secondCurrencyButton!!.setOnClickListener {
            val picker = CurrencyPicker.newInstance("Select Currency") // dialog title

            picker.setListener { _, code, symbol, _ ->
                (activity as MainActivity).mainFragment!!.setCurrency2(code)
                if (symbol != "0") {
                    (activity as MainActivity).mainFragment!!.setCurrencySymbol2(symbol)
                } else {
                    val secondCurrencySymbol =
                        (activity as MainActivity).mainFragment!!.fetchCurrencySymbol(code)
                    (activity as MainActivity).mainFragment!!.setCurrencySymbol2(
                        secondCurrencySymbol!!
                    )
                }
                Toast.makeText(
                    requireContext(),
                    "Second currency '$code' added. \nplease go to the main page and add an investment now!",
                    Toast.LENGTH_LONG
                ).show()
                (activity as? MainActivity)!!.updateViewPager()
                picker.dismiss()
            }
            picker.show(parentFragmentManager, "CURRENCY_PICKER")
        }

        aboutUsButton!!.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            transaction.replace(R.id.fragment2, AboutUs())
            transaction.addToBackStack(null) // if written, this transaction will be added to backstack
            transaction.commit()
        }


        return view
    }

    private fun convertToExcel(dbName: String, directoryPath: String) {
        val sqliteToExcel = SQLiteToExcel(requireContext(), dbName, directoryPath)
        sqliteToExcel.exportSingleTable("investment", "investment.xls", object : ExportListener {
            override fun onStart() {
                Log.d("InvestmentTracker", "Started the exporting process..")
            }

            override fun onCompleted(filePath: String) {
                Log.d("InvestmentTracker", "File is converted and saved at $filePath")
                sendFile(directoryPath)
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
                Log.e("InvestmentTracker", "Exception is $e")
            }
        })
    }

    private fun sendFile(directoryPath: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/excel"
                putExtra(Intent.EXTRA_TITLE, "Investment Tracker")
                val uri = FileProvider.getUriForFile(
                    requireContext(), fileProvider,
                    File(directoryPath + "/investment.xls")
                )
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivity(Intent.createChooser(intent, "Send Investment file"))
        } else {
            val intent = Intent(Intent.ACTION_SEND).apply {
                val uri = Uri.parse(directoryPath + "/investment.xls")
                type = "application/excel"
                putExtra(Intent.EXTRA_STREAM, uri)
            }
            startActivity(Intent.createChooser(intent, null))
        }
    }

    private fun createDirectoryPath(FolderName: String): File? {
        var dir: File?
        dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(requireContext().getExternalFilesDir(null).toString() + "/" + FolderName + "/")
        } else {
            File(requireContext().getExternalFilesDir(null).toString() + "/" + FolderName + "/")
        }

        // Make sure the path directory exists.
        if (!dir.exists()) {
            val success = dir.mkdirs()
            if (!success) {
                dir = null
            }
        }
        return dir
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun isStoragePermissionGranted(): Boolean {
        val permissionStorage = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(
                    requireContext().applicationContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.v("InvestmentTracker", "Permission is granted")
                true
            } else {
                Log.v("InvestmentTracker", "Permission is revoked")
                ActivityCompat.requestPermissions(
                    requireContext() as Activity,
                    permissionStorage,
                    1
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("InvestmentTracker", "Permission is granted")
            true
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showFab()
        (activity as MainActivity).showBottomNav()
    }
}


/*importButton!!.setOnClickListener {

           (activity as MainActivity).mainFragment!!.investmentHelper!!.getDatabaseHelper().open()
           val file = File(directoryPath)
           Log.d("InvestmentTracker","Directory path is $directoryPath")
           if (!file.exists()) {
               Log.d("InvestmentTracker","File doesn't exist!")
           }
           val excelToSQLite = ExcelToSQLite(requireContext().applicationContext, dbName,true)

           excelToSQLite.importFromFile(directoryPath+"/investment.xls", object : ImportListener {
               override fun onStart() {
               }
               override fun onCompleted(dbName: String) {
                   Toast.makeText(requireContext(),
                       "File is converted, check it out here: $dbName",Toast.LENGTH_LONG).show()
                   Log.d("InvestmentTracker", "File is converted and saved at $dbName")
               }
               override fun onError(e: java.lang.Exception) {
                   e.printStackTrace()
                   Log.e("InvestmentTracker","Exception is $e")
               }
           })
           (activity as MainActivity).mainFragment!!.investmentHelper!!.getDatabaseHelper().close()
       }*/

