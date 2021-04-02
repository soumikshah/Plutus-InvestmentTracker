package com.soumikshah.investmenttracker.view;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Investment;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.view.View.GONE;

public class InvestmentViewHolder extends RecyclerView.ViewHolder {
    public TextView investmentName;
    public TextView investmentAmount;
    public TextView investmentMedium;
    public TextView interestToBePaid;
    public TextView investmentCategory;
    public TextView investmentDate;
    public TextView investmentMonth;
    public RelativeLayout viewBackground, viewForeground;
    public LinearLayout otherDetails;
    public TextView viewBackgroundText;
    public ImageView viewBackgroundImage;
    int mExpandedPosition = -1;

    private InvestmentViewHolder(View view) {
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
        viewBackgroundText = view.findViewById(R.id.background_text);
        viewBackgroundImage = view.findViewById(R.id.background_image);
        otherDetails = view.findViewById(R.id.otherDetails);
        //timestamp = view.findViewById(R.id.timestamp);
    }

    void bind(Investment investment, int position){
        investmentName.setText(investment.getInvestmentName());
        investmentAmount.setText(String.format(Locale.ENGLISH,"Rs.%,d", investment.getInvestmentAmount()));
        String investmentMediumText = "Invested in: <b>"+investment.getInvestmentMedium()+"</b>";
        SimpleDateFormat sim = new SimpleDateFormat("dd/MM/YYYY",Locale.ENGLISH);
        String investmentCategoryText = "Investment Type: <b>"+investment.getInvestmentCategory()+"</b>";
        String investmentDateText = "Invested on: <b>"+sim.format(investment.getInvestmentDate())+"</b>";
        String investmentMonthText = "Invested for: <b>"+investment.getInvestmentMonth()+"</b>";
        String investmentInterestToBePaidText = "Interest to be paid: <b>"+investment.getInvestmentPercent()+"</b>";
        investmentMedium.setText(Html.fromHtml(investmentMediumText));
        investmentCategory.setText(Html.fromHtml(investmentCategoryText));
        investmentDate.setText(Html.fromHtml(investmentDateText));
        investmentMonth.setText(Html.fromHtml(investmentMonthText));
        interestToBePaid.setText(Html.fromHtml(investmentInterestToBePaidText));

        if(investment.getInvestmentPercent()==0.0){
            interestToBePaid.setVisibility(GONE);
        }else{
            interestToBePaid.setVisibility(View.VISIBLE);
        }
        if(investment.getInvestmentMonth() == 0){
            investmentMonth.setVisibility(GONE);
        }else{
            investmentMonth.setVisibility(View.VISIBLE);
        }
        if(investment.getInvestmentCategory().isEmpty()){
            investmentCategory.setVisibility(GONE);
        }else{
            investmentCategory.setVisibility(View.VISIBLE);
        }
        if(investment.getInvestmentDate()==0){
            investmentDate.setVisibility(GONE);
        }else{
            investmentDate.setVisibility(View.VISIBLE);
        }

        final boolean isExpanded = position==mExpandedPosition;

        otherDetails.setVisibility(isExpanded? View.VISIBLE:View.GONE);
        itemView.setActivated(isExpanded);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:position;

                if(investment.getInvestmentPercent()==0.0 && investment.getInvestmentMonth() == 0 && investment.getInvestmentCategory().isEmpty() && investment.getInvestmentDate()==0){
                    Toast.makeText(itemView.getContext(), R.string.toast_message_no_detail,Toast.LENGTH_LONG).show();
                }
                //notifyDataSetChanged();
            }
        });
    }
    static InvestmentViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.investment_list_row, parent, false);
        return new InvestmentViewHolder(view);
    }

}
