package com.soumikshah.investmenttracker.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Investment;

import java.util.ArrayList;
import java.util.List;

public class InvestmentDetailFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView investmentName;
    private List<Investment> investmentList = new ArrayList<>();
    private InvestmentDetailAdapter mAdapter;
    InvestmentDetailFragment(List<Investment> investmentList){
        this.investmentList = investmentList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_investment_detail, container, false);
        investmentName = view.findViewById(R.id.investment_category_name);
        recyclerView = view.findViewById(R.id.recycler_view);
        mAdapter = new InvestmentDetailAdapter(getActivity(),investmentList);
        if(investmentList!= null && investmentList.size()>0){
            investmentName.setText(investmentList.get(0).getInvestmentCategory());
        }

        recyclerView.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);

        return view;
    }
}
