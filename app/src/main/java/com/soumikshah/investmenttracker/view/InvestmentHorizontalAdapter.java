package com.soumikshah.investmenttracker.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Investment;

import java.util.List;

public class InvestmentHorizontalAdapter extends RecyclerView.Adapter<InvestmentHorizontalAdapter.MyViewHolder> {
    private Context context;
    private List<Investment> investmentData;

    InvestmentHorizontalAdapter(Context context, List<Investment> investmentData){
        this.context = context;
        this.investmentData = investmentData;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView placeInvestmentDone;
        public TextView amount;
        public MyViewHolder(@NonNull View view){
            super(view);
            name = view.findViewById(R.id.investmentName);
            placeInvestmentDone = view.findViewById(R.id.investmentPlace);
            amount = view.findViewById(R.id.amount);
        }

    }

    @NonNull
    @Override
    public  MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.investment_horizontal_box,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Investment invest = investmentData.get(position);
        holder.name.setText(invest.getInvestmentName());
        holder.placeInvestmentDone.setText(invest.getInvestmentMedium());
        holder.amount.setText(String.format(context.getResources().getString(R.string.rs)+"%,d", invest.getInvestmentAmount()));
    }

    @Override
    public int getItemCount() {
        return investmentData.size();
    }
}
