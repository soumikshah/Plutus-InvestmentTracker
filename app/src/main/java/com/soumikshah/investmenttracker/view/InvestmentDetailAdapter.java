package com.soumikshah.investmenttracker.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Investment;

import java.util.ArrayList;
import java.util.List;

public class InvestmentDetailAdapter extends RecyclerView.Adapter<InvestmentDetailAdapter.MyViewHolder> {
    private Context context;
    private List<Investment> investmentList = new ArrayList<>();

    InvestmentDetailAdapter(Context context, List<Investment> investmentList){
        this.context = context;
        this.investmentList = investmentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_investment_page,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Investment investment = investmentList.get(position);
        holder.investmentName.setText(investment.getInvestmentName());
        holder.investmentMedium.setText(investment.getInvestmentMedium());
        holder.investmentAmount.setText(String.format(context.getResources().getString(R.string.rs)+" %,d", investment.getInvestmentAmount()));
        //todo holder.parent && moredetails will open new fragment with details about clicked investment.
    }

    @Override
    public int getItemCount() {
        return investmentList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView investmentName;
        public TextView investmentMedium;
        public TextView investmentAmount;
        public TextView investmentMoreDetails;
        public RelativeLayout investmentParent;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            investmentName = itemView.findViewById(R.id.investment_name);
            investmentMedium = itemView.findViewById(R.id.investmentMedium);
            investmentAmount = itemView.findViewById(R.id.investmentAmount);
            investmentMoreDetails = itemView.findViewById(R.id.view_more);
            investmentParent = itemView.findViewById(R.id.parent);
        }
    }
}
