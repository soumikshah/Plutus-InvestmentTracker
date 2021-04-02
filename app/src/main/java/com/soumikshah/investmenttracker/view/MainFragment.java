package com.soumikshah.investmenttracker.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.DatabaseHelper;
import com.soumikshah.investmenttracker.database.model.Investment;
import com.soumikshah.investmenttracker.database.model.InvestmentViewModel;
import com.soumikshah.investmenttracker.utils.MyDividerItemDecoration;
import com.soumikshah.investmenttracker.utils.RecyclerItemTouchHelper;
import com.soumikshah.investmenttracker.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView noInvestmentView;
    //private InvestmentAdapter mAdapter;
    private TextView totalAmount;
    private TextView otherInvestment;
    private InvestmentViewModel investmentViewModel;
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;

    public List<Investment> getInvestmentsList() {
        return InvestmentsList;
    }

    private List<Investment> InvestmentsList = new ArrayList<>();

    public HashMap<String, Integer> getInvestmentTypeAndAmount() {
        return investmentTypeAndAmount;
    }

    private HashMap<String,Integer> investmentTypeAndAmount = new HashMap<>();
    private boolean restoreClickedOnSnackBar = false;
    private DatabaseHelper db;
    InvestmentListAdapter adapter;

    public MainFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.mainfragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        noInvestmentView = view.findViewById(R.id.empty_investment_view);
        totalAmount = view.findViewById(R.id.total_amount_invested);
        otherInvestment = view.findViewById(R.id.otherInvestment);

        db = new DatabaseHelper(getActivity());
        InvestmentsList.addAll(db.getAllInvestments());
        //mAdapter = new InvestmentAdapter(((MainActivity)getContext()), InvestmentsList);
        adapter = new InvestmentListAdapter(new InvestmentListAdapter.InvestmentDiff());
        totalAmount.setText(String.format(Locale.UK,"Rs.%,d", getInvestmentTotalAmount()));
        otherInvestment.setText(getInvestmentCategoryAndAmount());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(((MainActivity)getContext()), LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        investmentViewModel = new ViewModelProvider(getActivity()).get(InvestmentViewModel.class);
        investmentViewModel.getAllInvestments().observe(getViewLifecycleOwner(), new Observer<List<Investment>>() {
            @Override
            public void onChanged(List<Investment> investments) {
                InvestmentsList = investments;
                adapter.submitList(investments);
            }
        });
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
             view.callOnClick();
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT,
                        new RecyclerItemTouchHelper.RecyclerItemTouchHelperListener() {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

                if ((direction == ItemTouchHelper.LEFT) && (viewHolder instanceof InvestmentAdapter.MyViewHolder)) {

                    ((InvestmentAdapter.MyViewHolder) viewHolder).viewBackground.setBackgroundColor(getResources().getColor(R.color.bg_row_background));
                    ((InvestmentAdapter.MyViewHolder) viewHolder).viewBackgroundText.setText(R.string.delete);

                    // get the removed item name to display it in snack bar
                    String name = InvestmentsList.get(viewHolder.getAdapterPosition()).getInvestmentName();

                    // backup of removed item for undo purpose
                    final Investment deletedItem = InvestmentsList.get(viewHolder.getAdapterPosition());
                    final int deletedIndex = viewHolder.getAdapterPosition();

                    // remove the item from recycler view
                    //adapter.removeItem(viewHolder.getAdapterPosition());
                    // showing snack bar with Undo option
                    Snackbar snackbar = Snackbar
                            .make(((MainActivity) getActivity()).getCoordinatorLayout(), name + " removed from cart!", Snackbar.LENGTH_LONG);
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            //Deleting the entry from DB
                            if (!restoreClickedOnSnackBar) {
                                db.deleteInvestment(deletedItem);
                            }
                        }
                    });
                    snackbar.setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // undo is selected, restore the deleted item

                            restoreClickedOnSnackBar = true;
                            //adapter.restoreItem(deletedItem, deletedIndex);
                        }

                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
                if(direction == ItemTouchHelper.RIGHT){
                    if(getActivity()!= null){
                        if(viewHolder instanceof InvestmentAdapter.MyViewHolder){
                            ((InvestmentAdapter.MyViewHolder) viewHolder).viewBackgroundText.setText(R.string.edit_investment_title);
                            ((InvestmentAdapter.MyViewHolder) viewHolder).viewBackground.setBackgroundColor(Color.GREEN);
                        }
                        ((MainActivity)getActivity()).showInvestmentDialog(true, InvestmentsList.get(position), position);
                    }
                }
            }

        });
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);



        return view;
    }

    void createInvestment(String investmentName,
                          int investmentAmount,
                          float investmentPercent,
                          String investmentMedium,
                          String investmentCategory,
                          long investmentDate,
                          int investmentMonth) {
            int id = investmentViewModel.getInvestmentCount()+1;
            Log.d("Tracker",id+" : ");
            investmentViewModel.insertInvestment(new Investment(id,investmentName,investmentAmount,
                    investmentPercent,investmentMedium,investmentCategory,
                    investmentDate,investmentMonth));

    }

    void updateInvestment(String investment, int investmentAmount,
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
        adapter.notifyItemChanged(position);

        toggleEmptyInvestments();
    }

    private void deleteInvestment(int position) {
        // deleting the note from db
        db.deleteInvestment(InvestmentsList.get(position));

        // removing the note from the list
        InvestmentsList.remove(position);
        adapter.notifyItemRemoved(position);

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
     * Toggling list and empty investment view
     */
    private void toggleEmptyInvestments() {

        if (db.getInvestmentCount() > 0) {
            noInvestmentView.setVisibility(View.GONE);
        } else {
            noInvestmentView.setVisibility(View.VISIBLE);
        }
    }


    int getInvestmentTotalAmount(){
        int totalAmount =0;
        Investment investment;
        for(int i =0; i<getInvestmentsList().size(); i++){
            investment = getInvestmentsList().get(i);
            if(investmentTypeAndAmount != null){
                if(!investmentTypeAndAmount.containsKey(investment.getInvestmentCategory())){
                    investmentTypeAndAmount.put(investment.getInvestmentCategory(),investment.getInvestmentAmount());
                }else if(investmentTypeAndAmount.containsKey(investment.getInvestmentCategory())){
                    investmentTypeAndAmount.put(investment.getInvestmentCategory(),
                            investmentTypeAndAmount.get(investment.getInvestmentCategory())+investment.getInvestmentAmount());
                    }
            }

            totalAmount+=investment.getInvestmentAmount();
        }
        return totalAmount;
    }

    String getInvestmentCategoryAndAmount(){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,Integer> entry : investmentTypeAndAmount.entrySet()){
            String amount = String.format(Locale.ENGLISH,"Rs.%,d",entry.getValue());
            sb.append(entry.getKey()).append(" : ").append(amount).append("\n");
        }
        return sb.toString();
    }

    List<String> getInvestmentCategory(){
        List<String> investmentCategory = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : investmentTypeAndAmount.entrySet())
        {
            investmentCategory.add(entry.getKey());
        }
        return investmentCategory;
    }
}
