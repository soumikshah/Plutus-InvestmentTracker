package com.soumikshah.investmenttracker.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.soumikshah.investmenttracker.R;
import com.soumikshah.investmenttracker.database.model.Investment;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollLayoutManager;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.DiscreteScrollItemTransformer;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;

public class InvestmentCardActivity extends Fragment
        implements DiscreteScrollView.OnItemChangedListener<InvestmentCardAdapter.ViewHolder>{
    private ArrayList<Investment> investments;
    private Investment investment;
    private TextView itemName;
    private DiscreteScrollView itemPicker;
    private InfiniteScrollAdapter<?> infiniteScrollAdapter;
    private String investmentItemName = null;
    private Context context;
    InvestmentCardActivity(Context context,ArrayList<Investment> investments, String itemName){
        this.investments = investments;
        this.investmentItemName = itemName;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_details,container,false);
        itemName = view.findViewById(R.id.item_name);
        itemPicker = view.findViewById(R.id.item_picker);
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);
        itemName.setText(investmentItemName);
        infiniteScrollAdapter = InfiniteScrollAdapter.wrap(new InvestmentCardAdapter(context,investments));
        itemPicker.setAdapter(infiniteScrollAdapter);
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        return view;
    }

    @Override
    public void onCurrentItemChanged(@Nullable InvestmentCardAdapter.ViewHolder viewHolder, int adapterPosition) {

    }
}
