package com.soumikshah.investmenttracker.view;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Investment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;

public class InvestmentAdapter extends RecyclerView.Adapter<InvestmentAdapter.MyViewHolder> {
    private Context context;
    private List<Investment> InvestmentsList;
    int mExpandedPosition = -1;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView investmentName;
        public TextView investmentAmount;
        public TextView investmentMedium;
        public TextView interestToBePaid;
        public TextView investmentCategory;
        public TextView investmentDate;
        public TextView investmentMonth;
        public RelativeLayout viewBackground, viewForeground;
        public LinearLayout otherDetails;


        public MyViewHolder(View view) {
            super(view);
            investmentName = view.findViewById(R.id.investment);
            investmentAmount = view.findViewById(R.id.investmentAmount);
            investmentMedium = view.findViewById(R.id.investmentMedium);
            investmentCategory = view.findViewById(R.id.investmentCategory);
            investmentDate = view.findViewById(R.id.investedDate);
            investmentMonth = view.findViewById(R.id.investedNumberOfMonths);
            interestToBePaid = view.findViewById(R.id.interestToBePaid);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
            otherDetails = view.findViewById(R.id.otherDetails);
            //timestamp = view.findViewById(R.id.timestamp);
        }
    }

    public InvestmentAdapter(Context context, List<Investment> InvestmentsList) {
        this.context = context;
        this.InvestmentsList = InvestmentsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.investment_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Investment investment = InvestmentsList.get(position);

        holder.investmentName.setText(investment.getInvestmentName());
        holder.investmentAmount.setText(String.format("Rs.%s", String.valueOf(investment.getInvestmentAmount())));

        String investmentMedium = "Invested in: <b>"+investment.getInvestmentMedium()+"</b>";
        SimpleDateFormat sim = new SimpleDateFormat("dd/MM/YYYY",Locale.ENGLISH);
        String investmentCategory = "Investment Type: <b>"+investment.getInvestmentCategory()+"</b>";
        String investmentDate = "Invested on: <b>"+sim.format(investment.getInvestmentDate())+"</b>";
        String investmentMonth = "Invested for: <b>"+investment.getInvestmentMonth()+"</b>";
        String investmentInterestToBePaid = "Interest to be paid: <b>"+investment.getInvestmentPercent()+"</b>";

        holder.investmentMedium.setText(Html.fromHtml(investmentMedium));
        holder.investmentCategory.setText(Html.fromHtml(investmentCategory));
        holder.investmentDate.setText(Html.fromHtml(investmentDate));
        holder.investmentMonth.setText(Html.fromHtml(investmentMonth));
        holder.interestToBePaid.setText(Html.fromHtml(investmentInterestToBePaid));

        if(investment.getInvestmentPercent()==0.0){
            holder.interestToBePaid.setVisibility(GONE);
        }else{
            holder.interestToBePaid.setVisibility(View.VISIBLE);
        }
        if(investment.getInvestmentMonth() == 0){
            holder.investmentMonth.setVisibility(GONE);
        }else{
            holder.investmentMonth.setVisibility(View.VISIBLE);
        }
        if(investment.getInvestmentCategory().isEmpty()){
            holder.investmentCategory.setVisibility(GONE);
        }else{
            holder.investmentCategory.setVisibility(View.VISIBLE);
        }
        if(investment.getInvestmentDate()==0){
            holder.investmentDate.setVisibility(GONE);
        }else{
            holder.investmentDate.setVisibility(View.VISIBLE);
        }

        final boolean isExpanded = position==mExpandedPosition;
        Log.d("Tracker","Visibility is "+isExpanded + " : "+position);
        holder.otherDetails.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:position;

                if(investment.getInvestmentPercent()==0.0 && investment.getInvestmentMonth() == 0 && investment.getInvestmentCategory().isEmpty() && investment.getInvestmentDate()==0){
                    Toast.makeText(holder.itemView.getContext(),"No more information to show. You can long press on this investment and add some more details!",Toast.LENGTH_LONG).show();
                }
               notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return InvestmentsList.size();
    }

    public void removeItem(int position) {
        InvestmentsList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Investment item, int position) {
        InvestmentsList.add(position, item);
        notifyItemInserted(position);
    }

  /*  *//**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     *//*
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d",Locale.ENGLISH);
            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }*/
}
