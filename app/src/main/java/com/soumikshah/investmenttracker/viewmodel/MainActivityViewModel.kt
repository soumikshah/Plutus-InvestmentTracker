package com.soumikshah.investmenttracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.soumikshah.investmenttracker.database.model.Investment

class MainActivityViewModel: ViewModel() {

    private val investment: MutableLiveData<Investment>?= null

    fun getInvestment(): MutableLiveData<Investment>? {
        return investment
    }

}