package com.soumikshah.investmenttracker.view;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {
    private Context context;
    private List<Note> notesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView note;
        public TextView investmentAmount;
        public TextView interestToBePaid;
        public TextView dot;
        public TextView timestamp;
        public TextView investmentMedium;
        public TextView investmentCategory;
        public TextView investmentDate;
        public TextView investmentMonth;

        public MyViewHolder(View view) {
            super(view);
            note = view.findViewById(R.id.note);
            investmentAmount = view.findViewById(R.id.investmentAmount);
            interestToBePaid = view.findViewById(R.id.investmentInterest);
            investmentMedium = view.findViewById(R.id.investmentMedium);
            investmentCategory = view.findViewById(R.id.investmentCategory);
            investmentDate = view.findViewById(R.id.investedDate);
            investmentMonth = view.findViewById(R.id.investedNumberOfMonths);
            dot = view.findViewById(R.id.dot);
            //timestamp = view.findViewById(R.id.timestamp);
        }
    }

    public NotesAdapter(Context context, List<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note = notesList.get(position);

        holder.note.setText(note.getInvestmentName());
        holder.investmentAmount.setText(String.format("Rs.%s", String.valueOf(note.getInvestmentAmount())));
        holder.interestToBePaid.setText(String.format("%s%%", String.valueOf(note.getInvestmentPercent())));
        holder.investmentMedium.setText(String.format("Invested on: %s", note.getInvestmentMedium()));
        holder.investmentCategory.setText(String.format("Investment Type: %s", note.getInvestmentCategory()));
        SimpleDateFormat sim = new SimpleDateFormat("dd/MM/YYYY",Locale.ENGLISH);
        holder.investmentDate.setText(String.format("Invested on: %s", sim.format(note.getInvestmentDate())));
        holder.investmentMonth.setText(String.format(Locale.ENGLISH,"For %d months", note.getInvestmentMonth()));
        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));
        if(note.getInvestmentPercent()==0.0){
            holder.interestToBePaid.setVisibility(View.INVISIBLE);
        }
        // Formatting and displaying timestamp
        //holder.timestamp.setText(formatDate(note.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
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
