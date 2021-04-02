package com.soumikshah.investmenttracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.soumikshah.investmenttracker.database.model.Investment;

import java.util.List;
@Dao
public interface InvestmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInvestment(Investment investment);

    @Delete
    void deleteInvestment(Investment investment);

    @Query("SELECT * FROM investments")
    LiveData<List<Investment>> getAllInvestments();

    @Query("SELECT COUNT(*) FROM investments")
    int getInvestmentCount();

    @Query("SELECT * FROM investments WHERE(:id)")
    Investment getInvestment(long id);

    /*@Delete
    void onUpgrade();*/

    @Update
    int updateInvestment(Investment investment);


}
