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

import java.util.ArrayList;
import java.util.Locale;

public class InvestmentCardAdapter extends RecyclerView.Adapter<InvestmentCardAdapter.ViewHolder> {
    private ArrayList<Investment> investments;
    private Context context;
    public InvestmentCardAdapter(Context context, ArrayList<Investment> investments){
        this.investments = investments;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_investment_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Investment investment = investments.get(position);
        holder.investmentName.setText(String.format("Investment Name is : %s", investment.getInvestmentName()));
        holder.investmentAmount.setText(String.format(Locale.ENGLISH,"Investment Amount is : %,d", investment.getInvestmentAmount()));
        holder.investmentMedium.setText(String.format("Investment Medium is :%s", investment.getInvestmentMedium()));
        holder.investmentCategory.setText(String.format("Investment Category is :%s", investment.getInvestmentCategory()));
        //holder.investmentDate.setText(investment.getInvestmentDate());
    }

    @Override
    public int getItemCount() {
        return investments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView investmentName;
        private TextView investmentAmount;
        private TextView investmentMedium;
        private TextView investmentCategory;
        private TextView investmentDate;


        public ViewHolder(View itemView) {
            super(itemView);
            investmentName = itemView.findViewById(R.id.investmentName);
            investmentAmount = itemView.findViewById(R.id.investmentAmount);
            investmentCategory= itemView.findViewById(R.id.investmentCategory);
            investmentMedium = itemView.findViewById(R.id.investmentMedium);
            investmentDate = itemView.findViewById(R.id.investmentDate);
        }
    }
}
