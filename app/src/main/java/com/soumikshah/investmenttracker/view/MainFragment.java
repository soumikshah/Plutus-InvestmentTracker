package com.soumikshah.investmenttracker.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.DatabaseHelper;
import com.soumikshah.investmenttracker.database.model.Investment;
import com.soumikshah.investmenttracker.utils.MyDividerItemDecoration;
import com.soumikshah.investmenttracker.utils.RecyclerItemTouchHelper;
import com.soumikshah.investmenttracker.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView noInvestmentView;
    private InvestmentAdapter mAdapter;

    public List<Investment> getInvestmentsList() {
        return InvestmentsList;
    }

    private List<Investment> InvestmentsList = new ArrayList<>();
    private boolean restoreClickedOnSnackBar = false;
    private DatabaseHelper db;


    public MainFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainfragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        noInvestmentView = view.findViewById(R.id.empty_investment_view);
        db = new DatabaseHelper(getActivity());
        InvestmentsList.addAll(db.getAllInvestments());
        mAdapter = new InvestmentAdapter(((MainActivity)getContext()), InvestmentsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(((MainActivity)getContext()), LinearLayoutManager.VERTICAL, 16));
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
             view.callOnClick();
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, new RecyclerItemTouchHelper.RecyclerItemTouchHelperListener() {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

                if (viewHolder instanceof InvestmentAdapter.MyViewHolder) {
                    // get the removed item name to display it in snack bar
                    String name = InvestmentsList.get(viewHolder.getAdapterPosition()).getInvestmentName();

                    // backup of removed item for undo purpose
                    final Investment deletedItem = InvestmentsList.get(viewHolder.getAdapterPosition());
                    final int deletedIndex = viewHolder.getAdapterPosition();

                    // remove the item from recycler view
                    mAdapter.removeItem(viewHolder.getAdapterPosition());
                   // showing snack bar with Undo option
                    Snackbar snackbar = Snackbar
                            .make(((MainActivity)getActivity()).getCoordinatorLayout(), name + " removed from cart!", Snackbar.LENGTH_LONG);
                    snackbar.addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            //Deleting the entry from DB
                            if(!restoreClickedOnSnackBar){
                                db.deleteInvestment(deletedItem);
                            }
                        }
                    });
                    snackbar.setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // undo is selected, restore the deleted item

                            restoreClickedOnSnackBar = true;
                            mAdapter.restoreItem(deletedItem, deletedIndex);
                        }

                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
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
     * Toggling list and empty investment view
     */
    private void toggleEmptyInvestments() {

        if (db.getInvestmentCount() > 0) {
            noInvestmentView.setVisibility(View.GONE);
        } else {
            noInvestmentView.setVisibility(View.VISIBLE);
        }
    }
}
