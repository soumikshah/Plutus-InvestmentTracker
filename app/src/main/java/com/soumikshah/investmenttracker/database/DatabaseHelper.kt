package com.soumikshah.investmenttracker.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.soumikshah.investmenttracker.database.model.Investment
import java.util.*

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    //private var database:SQLiteDatabase? =null
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(Investment.CREATE_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        if(i ==1 && i==2){
            sqLiteDatabase.execSQL(Investment.CREATE_TEMP_TABLE)
            sqLiteDatabase.execSQL("INSERT INTO "+Investment.TEMP_TABLE_NAME+" select * from"+Investment.TABLE_NAME)
            sqLiteDatabase.execSQL("DROP TABLE "+Investment.TABLE_NAME)
            sqLiteDatabase.execSQL("ALTER TABLE "+Investment.TEMP_TABLE_NAME+" RENAME TO "+Investment.TABLE_NAME)
        }
    }

    /*@Throws(SQLException::class)
    fun open(): DatabaseHelper {
        database = this.getWritableDatabase()
        return this
    }

    override fun close(){
        database!!.close()
    }*/

    fun insertInvestment(investment: String?,
                         investmentAmount: Float,
                         investmentPercent: Float,
                         investmentMedium: String?,
                         investmentCategory: String?,
                         investmentDate: Long,
                         investmentMonth: Int,
                         investmentNumberOfUnits:String,
                         investmentPricePerUnit:Float,
                         investmentCurrency:String?): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(Investment.COLUMN_INVESTMENT, investment)
        values.put(Investment.COLUMN_INVESTMENT_AMOUNT, investmentAmount)
        values.put(Investment.COLUMN_INTEREST_PERCENT, investmentPercent)
        values.put(Investment.COLUMN_INVESTMENT_MEDIUM, investmentMedium)
        values.put(Investment.COLUMN_INVESTMENT_CATEGORY, investmentCategory)
        values.put(Investment.COLUMN_INVESTMENT_DATE, investmentDate)
        values.put(Investment.COLUMN_INVESTMENT_MONTH, investmentMonth)
        values.put(Investment.COLUMN_INVESTMENT_NUMBER_OF_UNITS, investmentNumberOfUnits)
        values.put(Investment.COLUMN_INVESTMENT_PRICE_PER_UNIT,investmentPricePerUnit)
        values.put(Investment.COLUMN_INVESTMENT_CURRENCY,investmentCurrency)
        val id = db.insert(Investment.TABLE_NAME, null, values)
        //db.close()
        return id
    }

    fun getInvestment(id: Long): Investment {
        val db = this.readableDatabase
        val cursor = db.query(Investment.TABLE_NAME, arrayOf(Investment.COLUMN_ID, Investment.COLUMN_INVESTMENT,
                Investment.COLUMN_INVESTMENT_AMOUNT, Investment.COLUMN_INTEREST_PERCENT,
                Investment.COLUMN_INVESTMENT_MEDIUM, Investment.COLUMN_INVESTMENT_CATEGORY,
                Investment.COLUMN_INVESTMENT_DATE, Investment.COLUMN_INVESTMENT_MONTH,
                Investment.COLUMN_INVESTMENT_NUMBER_OF_UNITS, Investment.COLUMN_INVESTMENT_PRICE_PER_UNIT,
                Investment.COLUMN_INVESTMENT_CURRENCY, Investment.COLUMN_TIMESTAMP),
                Investment.COLUMN_ID + "=?", arrayOf(id.toString()), null, null, null, null)
        cursor?.moveToFirst()
        val investment = Investment(
                cursor!!.getInt(cursor.getColumnIndex(Investment.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT)),
                cursor.getInt(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_AMOUNT)),
                cursor.getFloat(cursor.getColumnIndex(Investment.COLUMN_INTEREST_PERCENT)),
                cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_MEDIUM)),
                cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_CATEGORY)),
                cursor.getLong(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_DATE)),
                cursor.getInt(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_MONTH)),
                cursor.getString(cursor.getColumnIndex(Investment.COLUMN_TIMESTAMP)),
                cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_NUMBER_OF_UNITS)),
                cursor.getInt(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_PRICE_PER_UNIT)),
                cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_CURRENCY))
        )
        cursor.close()
        return investment
    }

    val allInvestments: List<Investment>
        get() {
            val investments: MutableList<Investment> = ArrayList()
            val selectQuery = "SELECT * FROM " + Investment.TABLE_NAME + " ORDER BY " + Investment.COLUMN_TIMESTAMP + " DESC"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val investment = Investment()
                    investment.id = cursor.getInt(cursor.getColumnIndex(Investment.COLUMN_ID))
                    investment.investmentName = cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT))
                    investment.investmentAmount = cursor.getFloat(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_AMOUNT))
                    investment.investmentPercent = cursor.getFloat(cursor.getColumnIndex(Investment.COLUMN_INTEREST_PERCENT))
                    investment.investmentMedium = cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_MEDIUM))
                    investment.investmentCategory = cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_CATEGORY))
                    investment.investmentDate = cursor.getLong(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_DATE))
                    investment.investmentMonth = cursor.getInt(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_MONTH))
                    investment.timestamp = cursor.getString(cursor.getColumnIndex(Investment.COLUMN_TIMESTAMP))
                    investment.investmentNumberOfUnits = cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_NUMBER_OF_UNITS))
                    investment.investmentPricePerUnit = cursor.getFloat(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_PRICE_PER_UNIT))
                    investment.investmentCurrency = cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_CURRENCY))
                    investments.add(investment)
                } while (cursor.moveToNext())
            }
            cursor.close()
            //db.close()
            return investments
        }
    val investmentCount: Int
        get() {
            val countQuery = "SELECT  * FROM " + Investment.TABLE_NAME
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)
            val count = cursor.count
            cursor.close()
            return count
        }

    fun updateInvestment(investment: Investment): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(Investment.COLUMN_INVESTMENT, investment.investmentName)
        values.put(Investment.COLUMN_INVESTMENT_AMOUNT, investment.investmentAmount)
        values.put(Investment.COLUMN_INTEREST_PERCENT, investment.investmentPercent)
        values.put(Investment.COLUMN_INVESTMENT_MEDIUM, investment.investmentMedium)
        values.put(Investment.COLUMN_INVESTMENT_CATEGORY, investment.investmentCategory)
        values.put(Investment.COLUMN_INVESTMENT_DATE, investment.investmentDate)
        values.put(Investment.COLUMN_INVESTMENT_MONTH, investment.investmentMonth)
        values.put(Investment.COLUMN_INVESTMENT_NUMBER_OF_UNITS, investment.investmentNumberOfUnits)
        values.put(Investment.COLUMN_INVESTMENT_PRICE_PER_UNIT, investment.investmentPricePerUnit)
        values.put(Investment.COLUMN_INVESTMENT_CURRENCY,investment.investmentCurrency)
        return db.update(Investment.TABLE_NAME, values, Investment.COLUMN_ID + " = ?", arrayOf(investment.id.toString()))
    }

    fun deleteInvestment(investment: Investment) {
        val db = this.writableDatabase
        db.delete(Investment.TABLE_NAME, Investment.COLUMN_ID + " = ?", arrayOf(investment.id.toString()))
        //db.close()
    }

    fun getTableName():String{
        return DATABASE_NAME
    }

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "investment_db"
    }
}