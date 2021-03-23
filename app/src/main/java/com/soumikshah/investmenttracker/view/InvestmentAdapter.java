package com.soumikshah.investmenttracker.view;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Investment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvestmentAdapter extends RecyclerView.Adapter<InvestmentAdapter.MyViewHolder> {
    private Context context;
    private List<Investment> InvestmentsList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView investmentName;
        public TextView investmentAmount;
        public TextView investmentMedium;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            investmentName = view.findViewById(R.id.investment);
            investmentAmount = view.findViewById(R.id.investmentAmount);
            investmentMedium = view.findViewById(R.id.investmentMedium);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Investment investment = InvestmentsList.get(position);

        holder.investmentName.setText(investment.getInvestmentName());
        holder.investmentAmount.setText(String.format("Rs.%s", String.valueOf(investment.getInvestmentAmount())));
        holder.investmentMedium.setText(String.format("Invested in: %s", investment.getInvestmentMedium()));
        /*SimpleDateFormat sim = new SimpleDateFormat("dd/MM/YYYY",Locale.ENGLISH);
        holder.investmentDate.setText(String.format("Invested on: %s", sim.format(investment.getInvestmentDate())));
        holder.investmentMonth.setText(String.format(Locale.ENGLISH,"For %d months", investment.getInvestmentMonth()));
        */
        // Formatting and displaying timestamp
        //holder.timestamp.setText(formatDate(note.getTimestamp()));
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

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
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
    }
}
