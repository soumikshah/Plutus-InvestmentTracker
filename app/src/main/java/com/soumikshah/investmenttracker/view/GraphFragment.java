package com.soumikshah.investmenttracker.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphFragment extends Fragment {

    PieChart pieChart;
    PieData pieData = null;
    HashMap<String,Integer> investmentList;
    public GraphFragment(){}
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphfragment, container, false);
        pieChart = view.findViewById(R.id.pieChart_view);
        if(getActivity()!=null){
            investmentList = ((MainActivity)getActivity()).mainFragment.getInvestmentTypeAndAmount();
        }else{
            investmentList = new HashMap<>();
        }

        initPieChart();
        showPieChart();
        return view;
    }

    private void showPieChart(){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "Type of investments";
        //input data and fit data into pie chart entry
        for(String type: investmentList.keySet()){
            pieEntries.add(new PieEntry(investmentList.get(type).floatValue(),type));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(getListOfColor());
        pieData = new PieData(pieDataSet);
        refreshPieChart(pieData);
    }
    //Initialize piechart
    private void initPieChart(){
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setRotationAngle(0);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        pieChart.setHoleColor(Color.parseColor("#000000"));
    }

    //Setting up colors for piechart
    ArrayList<Integer> getListOfColor(){
        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#304567"));
        colors.add(Color.parseColor("#309967"));
        colors.add(Color.parseColor("#476567"));
        colors.add(Color.parseColor("#890567"));
        colors.add(Color.parseColor("#a35567"));
        colors.add(Color.parseColor("#ff5f67"));
        colors.add(Color.parseColor("#3ca567"));
        return colors;
    }

    void refreshPieChart(PieData pieData){
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
