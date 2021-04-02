package com.soumikshah.investmenttracker.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.soumikshah.investmenttracker.database.dao.InvestmentDao;
import com.soumikshah.investmenttracker.database.model.Investment;

import java.util.List;

public class InvestmentRepository {
    private InvestmentDao investmentDao;
    private LiveData<List<Investment>> listLiveData;
    private int totalInvestmentCount =0;
    private Investment investmentDetail;

    public InvestmentRepository(Application application){
        InvestmentRoomDatabase db = InvestmentRoomDatabase.getDatabase(application);
        investmentDao = db.investmentDao();
        listLiveData = investmentDao.getAllInvestments();
    }

    public LiveData<List<Investment>> getAllInvestments(){
        if(listLiveData.getValue()!=null){
            for(Investment investment:listLiveData.getValue()){
                Log.d("Tracker",investment.getInvestmentName());
            }
        }

        return listLiveData;
    }

    public void insertInvestment(final Investment investment){
        InvestmentRoomDatabase.databaseWriteExecutor.execute(() ->{
            investmentDao.insertInvestment(investment);
        });
    }

    public void deleteInvestment(Investment investment){
        InvestmentRoomDatabase.databaseWriteExecutor.execute(() ->{
            investmentDao.deleteInvestment(investment);
        });
    }

    public int getInvestmentCount(){
        InvestmentRoomDatabase.databaseWriteExecutor.execute(()->{
            totalInvestmentCount = investmentDao.getInvestmentCount();
        });
        return totalInvestmentCount;
    }

    public Investment getInvestment(long id){
        InvestmentRoomDatabase.databaseWriteExecutor.execute(() ->{
            investmentDetail = investmentDao.getInvestment(id);
        });
        return investmentDetail;
    }

    public void onUpgrade(){
        /*InvestmentRoomDatabase.databaseWriteExecutor.execute(()->{
            investmentDao.onUpgrade();
        });*/
    }

    public void updateInvestment(Investment investment){
        InvestmentRoomDatabase.databaseWriteExecutor.execute(()->{
            investmentDao.updateInvestment(investment);
        });
    }
}
