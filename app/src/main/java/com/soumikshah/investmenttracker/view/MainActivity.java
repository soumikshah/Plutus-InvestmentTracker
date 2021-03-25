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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Investment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public CoordinatorLayout getCoordinatorLayout() {
        return coordinatorLayout;
    }

    private CoordinatorLayout coordinatorLayout;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MainFragment mainFragment;
    long investmentDateInLong = 0;
    DatePickerDialog datePickerDialog;
    float interestToBeReceived;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        if(getActionBar() !=null){
            getActionBar().setDisplayHomeAsUpEnabled(false);
        }
        mainFragment = new MainFragment();
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInvestmentDialog(false, null, -1);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mainFragment, "Mainpage");
        adapter.addFragment(new GraphFragment(), "Graph");
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    void showInvestmentDialog(final boolean shouldUpdate, final Investment investment, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
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
        gradientDrawable.setStroke(2,getResources().getColor(R.color.investment_name));

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

                if(inputInvestmentPercent.getText().toString().matches("")){
                    interestToBeReceived = 0;
                }else{
                    interestToBeReceived = Float.parseFloat(inputInvestmentPercent.getText().toString());
                }

                if(inputInvestmentMedium.getText().toString().isEmpty()){
                    inputInvestmentMedium.setText(R.string.not_mentioned);
                }
                if(inputInvestmentCategory.getText().toString().isEmpty()){
                    inputInvestmentCategory.setText("");
                }
                if(inputInvestmentNumberOfMonths.getText().toString().isEmpty()){
                    inputInvestmentNumberOfMonths.setText("0");
                }

                if (shouldUpdate && investment != null) {
                    mainFragment.updateInvestment(inputInvestmentName.getText().toString(),
                            Integer.parseInt(inputInvestmentAmount.getText().toString()),
                            interestToBeReceived,
                            inputInvestmentMedium.getText().toString(),
                            inputInvestmentCategory.getText().toString(),
                            investmentDateInLong,
                            Integer.parseInt(inputInvestmentNumberOfMonths.getText().toString()),
                            position);
                } else {
                    mainFragment.createInvestment(inputInvestmentName.getText().toString(),
                            Integer.parseInt(inputInvestmentAmount.getText().toString()),
                            interestToBeReceived,
                            inputInvestmentMedium.getText().toString(),
                            inputInvestmentCategory.getText().toString(),
                            investmentDateInLong,Integer.parseInt(inputInvestmentNumberOfMonths.getText().toString()));
                }
            }
        });
    }


}