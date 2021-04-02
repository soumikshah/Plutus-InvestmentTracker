package com.soumikshah.investmenttracker.database.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.soumikshah.investmenttracker.database.InvestmentRepository;

import java.util.List;

public class InvestmentViewModel extends AndroidViewModel {
    private InvestmentRepository investmentRepository;
    private final LiveData<List<Investment>> listLiveData;

    public InvestmentViewModel(Application application){
        super(application);
        investmentRepository = new InvestmentRepository(application);
        listLiveData = investmentRepository.getAllInvestments();
    }

    public LiveData<List<Investment>> getAllInvestments() {return listLiveData;}

    public void insertInvestment(Investment investment) {investmentRepository.insertInvestment(investment);}

    public void deleteInvestment(Investment investment) {investmentRepository.deleteInvestment(investment);}

    public int getInvestmentCount(){return investmentRepository.getInvestmentCount();}

    public Investment getInvestment(long id) {return investmentRepository.getInvestment(id);}

    public void onUpgrade(){investmentRepository.onUpgrade();}

    public void updateInvestment(Investment investment){investmentRepository.updateInvestment(investment);}
}
