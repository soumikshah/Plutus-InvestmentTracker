package com.soumikshah.investmenttracker.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Investment;

public class InvestmentListAdapter extends ListAdapter<Investment,InvestmentViewHolder> {
    public InvestmentListAdapter(@NonNull DiffUtil.ItemCallback<Investment> diffcallBack){
        super(diffcallBack);
    }

    @Override
    public InvestmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return InvestmentViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(InvestmentViewHolder holder, int position) {
        Investment current= getItem(position);
        holder.bind(current,position);
        //notifyDataSetChanged();
    }

    static class InvestmentDiff extends DiffUtil.ItemCallback<Investment> {

        @Override
        public boolean areItemsTheSame(@NonNull Investment oldItem, @NonNull Investment newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Investment oldItem, @NonNull Investment newItem) {
            return oldItem.getInvestmentName().equals(newItem.getInvestmentName());
        }
    }
}

