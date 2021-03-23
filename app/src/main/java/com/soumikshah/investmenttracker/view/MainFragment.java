package com.soumikshah.investmenttracker.view;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.DatabaseHelper;
import com.soumikshah.investmenttracker.database.model.Investment;
import com.soumikshah.investmenttracker.utils.MyDividerItemDecoration;
import com.soumikshah.investmenttracker.utils.RecyclerTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView noInvestmentView;
    private InvestmentAdapter mAdapter;
    private List<Investment> InvestmentsList = new ArrayList<>();

    private DatabaseHelper db;


    public MainFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainfragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        noInvestmentView = view.findViewById(R.id.empty_investment_view);
        db = new DatabaseHelper(getActivity());
        InvestmentsList.addAll(db.getAllInvestments());
        mAdapter = new InvestmentAdapter(getContext(), InvestmentsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);
        toggleEmptyInvestments();

        /*
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));


        return view;
    }

    void createInvestment(String investmentName,
                          float investmentAmount,
                          float investmentPercent,
                          String investmentMedium,
                          String investmentCategory,
                          long investmentDate,
                          int investmentMonth) {
        long id = db.insertInvestment(investmentName,investmentAmount,
                investmentPercent,investmentMedium,investmentCategory,
                investmentDate,investmentMonth);

        // get the newly inserted note from db
        Investment n = db.getInvestment(id);

        if (n != null) {
            // adding new note to array list at 0 position
            InvestmentsList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyInvestments();
        }
    }

    void updateInvestment(String investment, float investmentAmount,
                          float investmentPercent, String investmentMedium,
                          String investmentCategory,
                          long investmentDate,
                          int investmentMonth, int position) {
        Investment n = InvestmentsList.get(position);
        n.setInvestmentName(investment);
        n.setInvestmentAmount(investmentAmount);
        n.setInvestmentPercent(investmentPercent);
        n.setInvestmentMedium(investmentMedium);
        n.setInvestmentCategory(investmentCategory);
        n.setInvestmentDate(investmentDate);
        n.setInvestmentMonth(investmentMonth);

        // updating note in db
        db.updateInvestment(n);

        // refreshing the list
        InvestmentsList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyInvestments();
    }

    private void deleteInvestment(int position) {
        // deleting the note from db
        db.deleteInvestment(InvestmentsList.get(position));

        // removing the note from the list
        InvestmentsList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyInvestments();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence[] colors = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    ((MainActivity)getActivity()).showInvestmentDialog(true, InvestmentsList.get(position), position);
                } else {
                    deleteInvestment(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyInvestments() {
        // you can check notesList.size() > 0

        if (db.getInvestmentCount() > 0) {
            noInvestmentView.setVisibility(View.GONE);
        } else {
            noInvestmentView.setVisibility(View.VISIBLE);
        }
    }
}
