package com.soumikshah.investmenttracker.view;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Investment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GraphFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    PieChart pieChart;
    PieData pieData = null;
    HashMap<String,Integer> investmentMap;
    HashMap<String,Integer> copyOfInvestmentMap = new HashMap<>();
    List<String> investmentCategoryInAList;
    List<Investment> investments;
    ArrayList<PieEntry> pieEntries;
    PieDataSet pieDataSet;
    public GraphFragment(){}
    Spinner spinner;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphfragment, container, false);
        pieChart = view.findViewById(R.id.pieChart_view);
        spinner = view.findViewById(R.id.spinner);

        if(getActivity()!=null){
            investmentMap = ((MainActivity)getActivity()).mainFragment.investmentHelper.getInvestmentTypeAndAmount();
            investmentCategoryInAList = ((MainActivity)getActivity()).mainFragment.investmentHelper.getInvestmentCategory();
            investments = ((MainActivity)getActivity()).mainFragment.investmentHelper.getInvestmentsList();
            investmentCategoryInAList.add("All");
        }
        Collections.sort(investmentCategoryInAList);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,investmentCategoryInAList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

        initPieChart();
        showPieChart();
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        if(!item.equals("All")){
            getCategory(item);
        }else{
            getAllCategories();
        }
        resetChart();
        showPieChart();
    }

    void getAllCategories(){
        if(getActivity()!=null && investmentCategoryInAList!= null){
            if(investmentCategoryInAList.size()>=0 && !investmentMap.containsKey(investmentCategoryInAList.get(0))){
                investmentMap.clear();
                investmentMap.putAll(copyOfInvestmentMap);
            }
            copyOfInvestmentMap.putAll(investmentMap);
        }
    }
    void getCategory(String category){
        Collections.emptyMap();
        investmentMap.clear();
        for(int i =0; i<investments.size();i++){
            if(investments.get(i).getInvestmentCategory().equals(category)){
                investmentMap.put(investments.get(i).getInvestmentName(),investments.get(i).getInvestmentAmount());
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



    private void resetChart() {
        pieEntries.clear();
        if(pieData!=null){
           pieData = null;
        }
        pieChart.getData().clearValues();
        pieChart.clear();
        pieChart.invalidate();
    }


    private void showPieChart(){
        pieEntries = new ArrayList<>();
        String label = "";
        //input data and fit data into pie chart entry
        for(String type: investmentMap.keySet()){
            pieEntries.add(new PieEntry(investmentMap.get(type).floatValue(),type));
        }
        pieDataSet = new PieDataSet(pieEntries,label);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(getListOfColor());
        pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter());
        refreshPieChart(pieData);
    }
    //Initialize piechart
    private void initPieChart(){
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationEnabled(true);
        pieChart.setRotationAngle(180);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        pieChart.setHoleColor(Color.parseColor("#000000"));
        Legend legend = pieChart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setTextSize(16f);
        legend.setTextColor(Color.WHITE);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
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
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
