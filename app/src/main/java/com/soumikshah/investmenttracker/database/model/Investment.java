package com.soumikshah.investmenttracker.database.model;

import android.util.Log;

public class Investment {
    public static final String TABLE_NAME = "investment";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INVESTMENT = "investment_name";
    public static final String COLUMN_INVESTMENT_AMOUNT = "investment_amount";
    public static final String COLUMN_INTEREST_PERCENT = "investment_interest_percent";
    public static final String COLUMN_INVESTMENT_MEDIUM = "investment_medium";
    public static final String COLUMN_INVESTMENT_CATEGORY = "investment_category";
    public static final String COLUMN_TIMESTAMP = "investment_timestamp";
    public static final String COLUMN_INVESTMENT_DATE = "investment_date";
    public static final String COLUMN_INVESTMENT_MONTH = "investment_month";
    public static final String COLUMN_INVESTMENT_NUMBER_OF_UNITS = "investment_number_of_units";
    public static final String COLUMN_INVESTMENT_PRICE_PER_UNIT = "investment_price_per_unit";
    public static final String COLUMN_INVESTMENT_CURRENCY = "investment_currency";

    private int id;
    private String investmentName;
    private int investmentAmount;
    private float investmentPercent;
    private String investmentMedium;
    private String investmentCategory;
    private String timestamp;
    private long investmentDate;
    private int investmentMonth;
    private String investmentNumberOfUnits;
    private int investmentPricePerUnit;
    private String investmentCurrency;

    public static final String CREATE_TABLE =
            "CREATE TABLE "+TABLE_NAME
                    + "("
                    + COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_INVESTMENT+" TEXT,"
                    + COLUMN_INVESTMENT_AMOUNT+ " INTEGER,"
                    + COLUMN_INTEREST_PERCENT+ " FLOAT(1),"
                    + COLUMN_INVESTMENT_MEDIUM +" TEXT,"
                    + COLUMN_INVESTMENT_CATEGORY+ " TEXT,"
                    + COLUMN_INVESTMENT_DATE+ " INTEGER,"
                    + COLUMN_INVESTMENT_MONTH+ " INTEGER,"
                    + COLUMN_TIMESTAMP+ " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_INVESTMENT_NUMBER_OF_UNITS+ " TEXT,"
                    +COLUMN_INVESTMENT_PRICE_PER_UNIT+ " INTEGER,"
                    +COLUMN_INVESTMENT_CURRENCY+ " TEXT"
                    + ")";
    public Investment(){
    }

    public Investment(int id,
                      String investmentName,
                      int investmentAmount,
                      float investmentPercent,
                      String investmentMedium,
                      String investmentCategory,
                      Long investmentDate,
                      Integer investmentNumberOfMonths,
                      String timestamp,
                      String investmentNumberOfUnits,
                      int investmentPricePerUnit,
                      String investmentCurrency){

        this.id = id;
        this.investmentName = investmentName;
        this.investmentAmount = investmentAmount;
        this.investmentPercent = investmentPercent;
        this.investmentMedium = investmentMedium;
        this.investmentCategory = investmentCategory;
        this.investmentDate = investmentDate;
        this.investmentMonth = investmentNumberOfMonths;
        this.timestamp = timestamp;
        this.investmentNumberOfUnits = investmentNumberOfUnits;
        this.investmentPricePerUnit = investmentPricePerUnit;
        this.investmentCurrency = investmentCurrency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvestmentName() {
        return investmentName;
    }

    public void setInvestmentName(String investmentName) {
        this.investmentName = investmentName;
    }

    public int getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(int investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public float getInvestmentPercent() {
        return investmentPercent;
    }

    public void setInvestmentPercent(float investmentPercent) {
        this.investmentPercent = investmentPercent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getInvestmentMedium() {
        return investmentMedium;
    }

    public void setInvestmentMedium(String investmentMedium) {
        this.investmentMedium = investmentMedium;
    }

    public String getInvestmentCategory() {
        return investmentCategory;
    }

    public void setInvestmentCategory(String investmentCategory) {
        this.investmentCategory = investmentCategory;
    }


    public long getInvestmentDate() {
        Log.d("Tracker","Date is "+investmentDate);
        return investmentDate;
    }

    public void setInvestmentDate(long investmentDate) {
        this.investmentDate = investmentDate;
    }

    public int getInvestmentMonth() {
        return investmentMonth;
    }

    public void setInvestmentMonth(int investmentMonth) {
        this.investmentMonth = investmentMonth;
    }
    public String getInvestmentNumberOfUnits() {
        return investmentNumberOfUnits;
    }

    public void setInvestmentNumberOfUnits(String investmentNumberOfUnits) {
        this.investmentNumberOfUnits = investmentNumberOfUnits;
    }

    public int getInvestmentPricePerUnit() {
        return investmentPricePerUnit;
    }

    public void setInvestmentPricePerUnit(int investmentPricePerUnit) {
        this.investmentPricePerUnit = investmentPricePerUnit;
    }

    public String getInvestmentCurrency() {
        return investmentCurrency;
    }

    public void setInvestmentCurrency(String investmentCurrency) {
        this.investmentCurrency = investmentCurrency;
    }
}
