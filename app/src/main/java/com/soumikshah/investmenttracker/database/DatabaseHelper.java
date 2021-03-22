package com.soumikshah.investmenttracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.soumikshah.investmenttracker.database.model.Note;

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
        sqLiteDatabase.execSQL(Note.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+Note.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public long insertInvestment(String investment,
                                 float investmentAmount,
                                 float investmentPercent,
                                 String investmentMedium,
                                 String investmentCategory,
                                 long investmentDate,
                                 int investmentMonth){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Note.COLUMN_INVESTMENT,investment);
        values.put(Note.COLUMN_INVESTMENT_AMOUNT, investmentAmount);
        values.put(Note.COLUMN_INTEREST_PERCENT, investmentPercent);
        values.put(Note.COLUMN_INVESTMENT_MEDIUM, investmentMedium);
        values.put(Note.COLUMN_INVESTMENT_CATEGORY, investmentCategory);
        values.put(Note.COLUMN_INVESTMENT_DATE, investmentDate);
        values.put(Note.COLUMN_INVESTMENT_MONTH,investmentMonth);

        long id = db.insert(Note.TABLE_NAME,null,values);
        db.close();
        return id;
    }

    public Note getInvestment(long id){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(Note.TABLE_NAME, new String[]{Note.COLUMN_ID,Note.COLUMN_INVESTMENT,
                                Note.COLUMN_INVESTMENT_AMOUNT,Note.COLUMN_INTEREST_PERCENT,
                                Note.COLUMN_INVESTMENT_MEDIUM, Note.COLUMN_INVESTMENT_CATEGORY,
                                Note.COLUMN_INVESTMENT_DATE, Note.COLUMN_INVESTMENT_MONTH,
                                Note.COLUMN_TIMESTAMP},
                Note.COLUMN_ID+"=?",new String[]{String.valueOf(id)},null,null,null,null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        Note note = new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_INVESTMENT)),
                cursor.getFloat(cursor.getColumnIndex(Note.COLUMN_INVESTMENT_AMOUNT)),
                cursor.getFloat(cursor.getColumnIndex(Note.COLUMN_INTEREST_PERCENT)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_INVESTMENT_MEDIUM)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_INVESTMENT_CATEGORY)),
                cursor.getLong(cursor.getColumnIndex(Note.COLUMN_INVESTMENT_DATE)),
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_INVESTMENT_MONTH)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP))
        );
        cursor.close();
        return note;
    }

    public List<Note> getAllInvestments() {
        List<Note> investments = new ArrayList<>();

        String selectQuery = "SELECT * FROM "+Note.TABLE_NAME + " ORDER BY "+ Note.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setInvestmentName(cursor.getString(cursor.getColumnIndex(Note.COLUMN_INVESTMENT)));
                note.setInvestmentAmount(cursor.getFloat(cursor.getColumnIndex(Note.COLUMN_INVESTMENT_AMOUNT)));
                note.setInvestmentPercent(cursor.getFloat(cursor.getColumnIndex(Note.COLUMN_INTEREST_PERCENT)));
                note.setInvestmentMedium(cursor.getString(cursor.getColumnIndex(Note.COLUMN_INVESTMENT_MEDIUM)));
                note.setInvestmentCategory(cursor.getString(cursor.getColumnIndex(Note.COLUMN_INVESTMENT_CATEGORY)));
                note.setInvestmentDate(cursor.getLong(cursor.getColumnIndex(Note.COLUMN_INVESTMENT_DATE)));
                note.setInvestmentMonth(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_INVESTMENT_MONTH)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

                investments.add(note);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return investments;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Note.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_INVESTMENT, note.getInvestmentName());
        values.put(Note.COLUMN_INVESTMENT_AMOUNT,note.getInvestmentAmount());
        values.put(Note.COLUMN_INTEREST_PERCENT,note.getInvestmentPercent());
        values.put(Note.COLUMN_INVESTMENT_MEDIUM,note.getInvestmentMedium());
        values.put(Note.COLUMN_INVESTMENT_CATEGORY,note.getInvestmentCategory());
        values.put(Note.COLUMN_INVESTMENT_DATE,note.getInvestmentDate());
        values.put(Note.COLUMN_INVESTMENT_MONTH,note.getInvestmentMonth());

        return db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}
