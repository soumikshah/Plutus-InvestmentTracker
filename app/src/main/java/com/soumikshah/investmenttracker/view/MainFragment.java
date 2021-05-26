package com.soumikshah.investmenttracker.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.InvestmentHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainFragment extends Fragment {
    private TextView noInvestmentView;
    private PieChart pieChart;
    InvestmentHelper investmentHelper;
    ArrayList<PieEntry> pieEntries;
    PieDataSet pieDataSet;
    PieData pieData = null;
    HashMap<String,Integer> investmentMap;
    List<String> investmentCategories = new ArrayList<>();
    GraphFragment graphFragment;
    RecyclerView recyclerView;
    RelativeLayout fragment;

    private InvestmentCategoryAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.mainfragment, container, false);
        //noInvestmentView = view.findViewById(R.id.empty_investment_view);
        //private InvestmentAdapter mAdapter;
        TextView totalAmount = view.findViewById(R.id.total_amount_invested);
        TextView otherInvestment = view.findViewById(R.id.otherInvestment);
        pieChart = view.findViewById(R.id.pieChart_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        fragment = view.findViewById(R.id.fragment);
        investmentHelper = new InvestmentHelper(getActivity());
        totalAmount.setText(String.format(getResources().getString(R.string.rs)+"%,d", investmentHelper.getInvestmentTotalAmount()));
        otherInvestment.setText(investmentHelper.getInvestmentCategoryAndAmount());
        graphFragment = new GraphFragment();
        mAdapter = new InvestmentCategoryAdapter(getActivity(),investmentHelper.getInvestmentsList() , investmentCategories);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        investmentMap = investmentHelper.getInvestmentTypeAndAmount();
        if(investmentMap!=null){
            for(String type : investmentMap.keySet()){
                if(!investmentCategories.contains(type)){
                    Log.d("Tracker","Here: "+type);
                    investmentCategories.add(type);
                }
            }
        }
        initPieChart();
        showPieChart();
        mAdapter.notifyDataSetChanged();
        return view;
    }

    private void initPieChart(){
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationEnabled(true);
        pieChart.setRotationAngle(180);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setDrawCenterText(false);
        pieChart.getLegend().setEnabled(false);
    }

    private void showPieChart(){
        pieEntries = new ArrayList<>();
        String label = "";
        //input data and fit data into pie chart entry
        for(String type: investmentMap.keySet()){
            Log.d("Tracker","Type: "+type);
            pieEntries.add(new PieEntry(investmentMap.get(type).floatValue(),type));
        }
        pieDataSet = new PieDataSet(pieEntries,label);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(graphFragment.getListOfColor());
        pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);
        pieDataSet.setSliceSpace(0.1f);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

}
