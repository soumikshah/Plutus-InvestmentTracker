package com.soumikshah.investmenttracker.view;

import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Investment;

import java.util.ArrayList;
import java.util.List;

public class InvestmentCategoryAdapter extends RecyclerView.Adapter<InvestmentCategoryAdapter.MyViewHolder> {
    private final List<Investment> investmentList;
    private final List<String> investmentCategory;
    private  Context context;
    InvestmentCategoryAdapter(Context context, List<Investment> investmentList, List<String> investmentCategory){
        this.context = context;
        this.investmentList = investmentList;
        this.investmentCategory = investmentCategory;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.investment_detail_box,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder,final int position) {
        if(investmentCategory.size()<=position)return;
        String investmentCat = investmentCategory.get(position);
        holder.investmentCategory.setText(String.format("%s "+context.getResources().getString(R.string.arrow), investmentCat));

        holder.setIsRecyclable(false);

        final ArrayList<Investment> investmentData = new ArrayList<>();
        for(Investment i : investmentList){
            if(i.getInvestmentCategory().equals(investmentCat)){
                investmentData.add(i);
            }
        }
        InvestmentHorizontalAdapter investmentHorizontalAdapter = new InvestmentHorizontalAdapter(context, investmentData);
        holder.horizontalView.setHasFixedSize(true);
        holder.horizontalView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.horizontalView.setAdapter(investmentHorizontalAdapter);
        holder.investmentCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment someFragment = new InvestmentDetailFragment(investmentData);
                FragmentTransaction transaction = ((MainActivity)context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, someFragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return investmentList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView investmentCategory;
        public RecyclerView horizontalView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            investmentCategory = itemView.findViewById(R.id.category_of_the_investment);
            horizontalView = itemView.findViewById(R.id.horizontal_view);
        }
    }
}
