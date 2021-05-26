package com.soumikshah.investmenttracker.database;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Investment;
import com.soumikshah.investmenttracker.view.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InvestmentHelper {

    public List<Investment> getInvestmentsList() {
        return InvestmentsList;
    }
    private final List<Investment> InvestmentsList = new ArrayList<>();
    private DatabaseHelper db;
    boolean nullDb = false;
    public HashMap<String, Integer> getInvestmentTypeAndAmount() {
        return investmentTypeAndAmount;
    }
    private final HashMap<String,Integer> investmentTypeAndAmount = new HashMap<>();
    Context context;


    public InvestmentHelper(Context context){
        this.context = context;
        db = new DatabaseHelper(context);
        InvestmentsList.addAll(db.getAllInvestments());
    }
    public void createInvestment(String investmentName,
                                 int investmentAmount,
                                 float investmentPercent,
                                 String investmentMedium,
                                 String investmentCategory,
                                 long investmentDate,
                                 int investmentMonth) {
        if(db != null){
            long id = db.insertInvestment(investmentName,investmentAmount,
                    investmentPercent,investmentMedium,investmentCategory,
                    investmentDate,investmentMonth);

            // get the newly inserted note from db
            Investment n = db.getInvestment(id);

            if (n != null) {
                // adding new note to array list at 0 position
                InvestmentsList.add(0, n);
                // refreshing the list
                toggleEmptyInvestments();
            }
        }
    }

    public void updateInvestment(String investment, int investmentAmount,
                                 float investmentPercent, String investmentMedium,
                                 String investmentCategory,
                                 long investmentDate,
                                 int investmentMonth, int position) {
        Investment n = InvestmentsList.get(position);
        n.setInvestmentName(investment);
        n.setInvestmentAmount(investmentAmount);
        n.setInvestmentPercent(investmentPercent);
        n.setInvestmentMedium(investmentMedium);
        n.setInvestmentCategory(investmentCategory);
        n.setInvestmentDate(investmentDate);
        n.setInvestmentMonth(investmentMonth);

        // updating note in db
        db.updateInvestment(n);

        // refreshing the list
        InvestmentsList.set(position, n);
        toggleEmptyInvestments();
    }

    private void deleteInvestment(int position) {
        // deleting the note from db
        db.deleteInvestment(InvestmentsList.get(position));

        // removing the note from the list
        InvestmentsList.remove(position);
        toggleEmptyInvestments();
    }

    private void toggleEmptyInvestments() {

        if (db.getInvestmentCount() > 0) {
            nullDb = false;
        } else {
            nullDb = true;
        }
    }

    public int getInvestmentTotalAmount(){
        int totalAmount =0;
        Investment investment;
        for(int i =0; i<getInvestmentsList().size(); i++){
            investment = getInvestmentsList().get(i);
            if(investmentTypeAndAmount != null){
                if(!investmentTypeAndAmount.containsKey(investment.getInvestmentCategory())){
                    investmentTypeAndAmount.put(investment.getInvestmentCategory(),investment.getInvestmentAmount());
                }else if(investmentTypeAndAmount.containsKey(investment.getInvestmentCategory())){
                    investmentTypeAndAmount.put(investment.getInvestmentCategory(),
                            investmentTypeAndAmount.get(investment.getInvestmentCategory())+investment.getInvestmentAmount());
                }
            }

            totalAmount+=investment.getInvestmentAmount();
        }
        return totalAmount;
    }

    public String getInvestmentCategoryAndAmount(){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,Integer> entry : investmentTypeAndAmount.entrySet()){
            String amount = String.format(context.getResources().getString(R.string.rs)+"%,d",entry.getValue());
            sb.append(entry.getKey()).append(" : ").append(amount).append("\n");
        }
        return sb.toString();
    }

    public List<String> getInvestmentCategory(){
        List<String> investmentCategory = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : investmentTypeAndAmount.entrySet())
        {
            investmentCategory.add(entry.getKey());
        }
        return investmentCategory;
    }
}
