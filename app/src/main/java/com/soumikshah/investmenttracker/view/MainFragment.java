package com.soumikshah.investmenttracker.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.soumikshah.investmenttracker.database.InvestmentHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainFragment extends Fragment {
    private TextView noInvestmentView;
    private PieChart pieChart;
    InvestmentHelper investmentHelper;
    ArrayList<PieEntry> pieEntries;
    PieDataSet pieDataSet;
    PieData pieData = null;
    HashMap<String,Integer> investmentMap;
    GraphFragment graphFragment;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.mainfragment, container, false);
        //noInvestmentView = view.findViewById(R.id.empty_investment_view);
        //private InvestmentAdapter mAdapter;
        TextView totalAmount = view.findViewById(R.id.total_amount_invested);
        TextView otherInvestment = view.findViewById(R.id.otherInvestment);
        pieChart = view.findViewById(R.id.pieChart_view);
        investmentHelper = new InvestmentHelper(getActivity());
        totalAmount.setText(String.format(getResources().getString(R.string.rs)+"%,d", investmentHelper.getInvestmentTotalAmount()));
        otherInvestment.setText(investmentHelper.getInvestmentCategoryAndAmount());
        graphFragment = new GraphFragment();
        investmentMap = investmentHelper.getInvestmentTypeAndAmount();
        //mAdapter = new InvestmentAdapter(((MainActivity)getContext()), InvestmentsList);
        initPieChart();
        showPieChart();

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
