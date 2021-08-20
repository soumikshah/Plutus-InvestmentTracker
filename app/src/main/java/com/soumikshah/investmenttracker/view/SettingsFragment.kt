package com.soumikshah.investmenttracker.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.ajts.androidmads.library.SQLiteToExcel
import com.ajts.androidmads.library.SQLiteToExcel.ExportListener
import com.soumikshah.investmenttracker.R
import java.io.File


class SettingsFragment internal constructor() : Fragment(){
    private var sampleTextView:TextView? =null
    private var exportButton:Button? = null
    private var importButton:Button? = null
    private val fileProvider = "com.soumikshah.investmenttracker.provider"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        sampleTextView = view.findViewById(R.id.sampleTextView)
        exportButton = view.findViewById(R.id.exportButton)
        importButton = view.findViewById(R.id.importButton)

        sampleTextView!!.text = getString(R.string.sampletext)

        val dbName:String = (activity as MainActivity).mainFragment!!.investmentHelper!!.getTableNameFromDatabaseHelper()
        val directoryPath = createDirectoryPath("InvestmentTracker")!!.absolutePath

        exportButton!!.setOnClickListener {
            //Check for permission only when user clicks on export button.
            isStoragePermissionGranted()

            try{
                Thread.sleep(2000)
            }catch (exception: Exception){
                exception.printStackTrace()
                Log.e("InvestmentTracker","${exception.message}")
            }
            //Converting to excel and then calling send intent when converting is completed.
            convertToExcel(dbName,directoryPath)

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
        return view
    }

    private fun convertToExcel(dbName:String,directoryPath:String){
        val sqliteToExcel = SQLiteToExcel(requireContext(), dbName,directoryPath)
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
                Log.e("InvestmentTracker","Exception is $e")
            }
        })
    }

    private fun sendFile(directoryPath: String){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
            val intent = Intent(Intent.ACTION_SEND).apply {
                type="application/excel"
                putExtra(Intent.EXTRA_TITLE,"Investment Tracker")
                val uri = FileProvider.getUriForFile(requireContext(),fileProvider,
                    File(directoryPath+"/investment.xls"))
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivity(Intent.createChooser(intent,"Send Investment file"))
        }else{
            val intent = Intent(Intent.ACTION_SEND).apply {
                val uri = Uri.parse(directoryPath+"/investment.xls")
                type = "application/excel"
                putExtra(Intent.EXTRA_STREAM,uri)
            }
            startActivity(Intent.createChooser(intent,null))
        }
    }

    private fun createDirectoryPath(FolderName: String): File? {
        var dir: File?
        dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(requireContext().getExternalFilesDir(null).toString() + "/" + FolderName+"/")
        } else {
            File(requireContext().getExternalFilesDir(null).toString() + "/" + FolderName+"/")
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

    private fun isStoragePermissionGranted(): Boolean {
        val permissionStorage = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(requireContext().applicationContext,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                Log.v("InvestmentTracker", "Permission is granted")
                true
            } else {
                Log.v("InvestmentTracker", "Permission is revoked")
                ActivityCompat.requestPermissions(requireContext() as Activity, permissionStorage, 1)
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
    }
}