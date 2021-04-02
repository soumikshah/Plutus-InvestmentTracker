package com.soumikshah.investmenttracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.soumikshah.investmenttracker.database.model.Investment;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "investment_db";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Investment.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ Investment.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public long insertInvestment(String investment,
                                 int investmentAmount,
                                 float investmentPercent,
                                 String investmentMedium,
                                 String investmentCategory,
                                 long investmentDate,
                                 int investmentMonth){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Investment.COLUMN_INVESTMENT,investment);
        values.put(Investment.COLUMN_INVESTMENT_AMOUNT, investmentAmount);
        values.put(Investment.COLUMN_INTEREST_PERCENT, investmentPercent);
        values.put(Investment.COLUMN_INVESTMENT_MEDIUM, investmentMedium);
        values.put(Investment.COLUMN_INVESTMENT_CATEGORY, investmentCategory);
        values.put(Investment.COLUMN_INVESTMENT_DATE, investmentDate);
        values.put(Investment.COLUMN_INVESTMENT_MONTH,investmentMonth);

        long id = db.insert(Investment.TABLE_NAME,null,values);
        db.close();
        return id;
    }

    public Investment getInvestment(long id){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(Investment.TABLE_NAME, new String[]{Investment.COLUMN_ID, Investment.COLUMN_INVESTMENT,
                                Investment.COLUMN_INVESTMENT_AMOUNT, Investment.COLUMN_INTEREST_PERCENT,
                                Investment.COLUMN_INVESTMENT_MEDIUM, Investment.COLUMN_INVESTMENT_CATEGORY,
                                Investment.COLUMN_INVESTMENT_DATE, Investment.COLUMN_INVESTMENT_MONTH,
                                Investment.COLUMN_TIMESTAMP},
                Investment.COLUMN_ID+"=?",new String[]{String.valueOf(id)},null,null,null,null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        Investment investment = new Investment(
                cursor.getInt(cursor.getColumnIndex(Investment.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT)),
                cursor.getInt(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_AMOUNT)),
                cursor.getFloat(cursor.getColumnIndex(Investment.COLUMN_INTEREST_PERCENT)),
                cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_MEDIUM)),
                cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_CATEGORY)),
                cursor.getLong(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_DATE)),
                cursor.getInt(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_MONTH))
        );
        cursor.close();
        return investment;
    }

    public List<Investment> getAllInvestments() {
        List<Investment> investments = new ArrayList<>();

        String selectQuery = "SELECT * FROM "+ Investment.TABLE_NAME + " ORDER BY "+ Investment.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                Investment investment = new Investment();
                investment.setId(cursor.getInt(cursor.getColumnIndex(Investment.COLUMN_ID)));
                investment.setInvestmentName(cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT)));
                investment.setInvestmentAmount(cursor.getInt(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_AMOUNT)));
                investment.setInvestmentPercent(cursor.getFloat(cursor.getColumnIndex(Investment.COLUMN_INTEREST_PERCENT)));
                investment.setInvestmentMedium(cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_MEDIUM)));
                investment.setInvestmentCategory(cursor.getString(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_CATEGORY)));
                investment.setInvestmentDate(cursor.getLong(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_DATE)));
                investment.setInvestmentMonth(cursor.getInt(cursor.getColumnIndex(Investment.COLUMN_INVESTMENT_MONTH)));
                investment.setTimestamp(cursor.getString(cursor.getColumnIndex(Investment.COLUMN_TIMESTAMP)));

                investments.add(investment);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return investments;
    }

    public int getInvestmentCount() {
        String countQuery = "SELECT  * FROM " + Investment.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int updateInvestment(Investment investment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Investment.COLUMN_INVESTMENT, investment.getInvestmentName());
        values.put(Investment.COLUMN_INVESTMENT_AMOUNT, investment.getInvestmentAmount());
        values.put(Investment.COLUMN_INTEREST_PERCENT, investment.getInvestmentPercent());
        values.put(Investment.COLUMN_INVESTMENT_MEDIUM, investment.getInvestmentMedium());
        values.put(Investment.COLUMN_INVESTMENT_CATEGORY, investment.getInvestmentCategory());
        values.put(Investment.COLUMN_INVESTMENT_DATE, investment.getInvestmentDate());
        values.put(Investment.COLUMN_INVESTMENT_MONTH, investment.getInvestmentMonth());

        return db.update(Investment.TABLE_NAME, values, Investment.COLUMN_ID + " = ?",
                new String[]{String.valueOf(investment.getId())});
    }

    public void deleteInvestment(Investment investment) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Investment.TABLE_NAME, Investment.COLUMN_ID + " = ?",
                new String[]{String.valueOf(investment.getId())});
        db.close();
    }
}
