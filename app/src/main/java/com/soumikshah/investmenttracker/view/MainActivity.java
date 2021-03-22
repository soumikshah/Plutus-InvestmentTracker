package com.soumikshah.investmenttracker.view;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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


public class MainActivity extends AppCompatActivity {
    private InvestmentAdapter mAdapter;
    private List<Investment> InvestmentsList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noInvestmentView;
    DatePickerDialog datePickerDialog;
    private DatabaseHelper db;
    long investmentDateInLong = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noInvestmentView = findViewById(R.id.empty_investment_view);

        db = new DatabaseHelper(this);

        InvestmentsList.addAll(db.getAllInvestments());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInvestmentDialog(false, null, -1);
            }
        });

        mAdapter = new InvestmentAdapter(this, InvestmentsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyInvestments();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    private void createInvestment(String investmentName,
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

    private void updateInvestment(String investment, float investmentAmount,
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
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showInvestmentDialog(true, InvestmentsList.get(position), position);
                } else {
                    deleteInvestment(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showInvestmentDialog(final boolean shouldUpdate, final Investment investment, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.investment_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputInvestmentName = view.findViewById(R.id.investment);
        final EditText inputInvestmentAmount = view.findViewById(R.id.investmentAmount);
        final EditText inputInvestmentPercent = view.findViewById(R.id.investmentInterest);
        final EditText inputInvestmentMedium = view.findViewById(R.id.investmentMedium);
        final EditText inputInvestmentCategory = view.findViewById(R.id.investmentCategory);
        final TextView inputInvestmentDate = view.findViewById(R.id.investedDate);
        final EditText inputInvestmentNumberOfMonths = view.findViewById(R.id.investedNumberOfMonths);


        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.new_investment_title) : getString(R.string.edit_investment_title));

        GradientDrawable gradientDrawable = (GradientDrawable) inputInvestmentName.getBackground();
        gradientDrawable.setStroke(2,getResources().getColor(R.color.investment_list_text));

        GradientDrawable gradientDrawable1 = (GradientDrawable)inputInvestmentAmount.getBackground();
        gradientDrawable1.setStroke(2, getResources().getColor(R.color.investment_amount));

        GradientDrawable gradientDrawable2 = (GradientDrawable) inputInvestmentPercent.getBackground();
        gradientDrawable2.setStroke(2, getResources().getColor(R.color.investment_interest_percent));

        GradientDrawable gradientDrawable3 = (GradientDrawable) inputInvestmentMedium.getBackground();
        gradientDrawable3.setStroke(2,getResources().getColor(R.color.investment_medium));

        GradientDrawable gradientDrawable4 = (GradientDrawable) inputInvestmentCategory.getBackground();
        gradientDrawable4.setStroke(2,getResources().getColor(R.color.investment_category));

        GradientDrawable gradientDrawable5 = (GradientDrawable) inputInvestmentNumberOfMonths.getBackground();
        gradientDrawable5.setStroke(2,getResources().getColor(R.color.investment_amount_of_month));

        GradientDrawable gradientDrawable6 = (GradientDrawable) inputInvestmentDate.getBackground();
        gradientDrawable6.setStroke(2,getResources().getColor(R.color.investment_datepicker));

        inputInvestmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                inputInvestmentDate.setText(String.format(Locale.ENGLISH,"%d/%d/%d", dayOfMonth, monthOfYear + 1, year));
                                investmentDateInLong = c.getTimeInMillis();
                                Log.d("InvestmentTracker",investmentDateInLong+"");
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        if (shouldUpdate && investment != null) {
            inputInvestmentName.setText(investment.getInvestmentName());
            inputInvestmentAmount.setText(String.valueOf(investment.getInvestmentAmount()));
            inputInvestmentPercent.setText(String.valueOf(investment.getInvestmentPercent()));
            inputInvestmentMedium.setText(String.valueOf(investment.getInvestmentMedium()));
            inputInvestmentCategory.setText(String.valueOf(investment.getInvestmentCategory()));
            SimpleDateFormat sim = new SimpleDateFormat("dd/MM/YYYY",Locale.ENGLISH);
            inputInvestmentDate.setText(sim.format(investment.getInvestmentDate()));
            inputInvestmentNumberOfMonths.setText(String.valueOf(investment.getInvestmentMonth()));
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputInvestmentName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter investment name!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(inputInvestmentAmount.getText().toString())){
                    Toast.makeText(MainActivity.this,"Enter investment amount!", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
                float interestToBeReceived;
                if(inputInvestmentPercent.getText().toString().matches("")){
                     interestToBeReceived = 0;
                }else{
                    interestToBeReceived = Float.parseFloat(inputInvestmentPercent.getText().toString());
                }

                // check if user updating note
                if (shouldUpdate && investment != null) {
                    // update note by it's id
                    updateInvestment(inputInvestmentName.getText().toString(),
                            Float.parseFloat(inputInvestmentAmount.getText().toString()),
                            interestToBeReceived,
                            inputInvestmentMedium.getText().toString(),
                            inputInvestmentCategory.getText().toString(),
                            investmentDateInLong,
                            Integer.parseInt(inputInvestmentNumberOfMonths.getText().toString()),
                            position);
                } else {
                    // create new note
                    createInvestment(inputInvestmentName.getText().toString(),
                            Float.parseFloat(inputInvestmentAmount.getText().toString()),
                            interestToBeReceived,
                            inputInvestmentMedium.getText().toString(),
                            inputInvestmentCategory.getText().toString(),
                            investmentDateInLong,Integer.parseInt(inputInvestmentNumberOfMonths.getText().toString()));
                }
            }
        });
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