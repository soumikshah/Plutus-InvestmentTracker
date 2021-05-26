package com.soumikshah.investmenttracker.view;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private BottomNavigationView bottomNavigationView;
    MainFragment mainFragment;
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
            getActionBar().hide();
        }
        mainFragment = new MainFragment();
        viewPager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setupViewPager(viewPager);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.mainPage:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.graph:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.settings:
                        viewPager.setCurrentItem(2);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return true;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.mainPage).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.graph).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.settings).setChecked(true);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

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
        adapter.addFragment(new SettingsFragment(),"Settings");
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

        inputInvestmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(MainActivity.this,R.style.DatePickerTheme,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                inputInvestmentDate.setText(String.format(Locale.ENGLISH,"%d/%d/%d", dayOfMonth, monthOfYear + 1, year));
                                investmentDateInLong = c.getTimeInMillis();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
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
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    alertDialog.dismiss();
                }
                return false;
            }
        });
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputInvestmentName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Investment Name!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(inputInvestmentAmount.getText().toString())){
                    Toast.makeText(MainActivity.this,"Enter Investment Amount!", Toast.LENGTH_LONG).show();
                    return;
                } else if(TextUtils.isEmpty(inputInvestmentCategory.getText().toString())){
                    Toast.makeText(MainActivity.this,"Enter Investment Type!",Toast.LENGTH_LONG).show();
                }else {
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
                    mainFragment.investmentHelper.updateInvestment(inputInvestmentName.getText().toString(),
                            Integer.parseInt(inputInvestmentAmount.getText().toString()),
                            interestToBeReceived,
                            inputInvestmentMedium.getText().toString(),
                            inputInvestmentCategory.getText().toString(),
                            investmentDateInLong,
                            Integer.parseInt(inputInvestmentNumberOfMonths.getText().toString()),
                            position);
                } else {
                    mainFragment.investmentHelper.createInvestment(inputInvestmentName.getText().toString(),
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