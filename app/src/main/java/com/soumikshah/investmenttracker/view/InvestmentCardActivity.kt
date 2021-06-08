package com.soumikshah.investmenttracker.view

import com.soumikshah.investmenttracker.database.model.Investment
import com.yarolegovich.discretescrollview.DiscreteScrollView.OnItemChangedListener
import android.widget.TextView
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.soumikshah.investmenttracker.R
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import java.util.ArrayList

class InvestmentCardActivity internal constructor(
    private val investments: ArrayList<Investment>,
    itemName: String?
) : Fragment(), OnItemChangedListener<InvestmentCardAdapter.ViewHolder?> {
    private val investment: Investment? = null
    private var itemName: TextView? = null
    private var itemPicker: DiscreteScrollView? = null
    private var infiniteScrollAdapter: InfiniteScrollAdapter<*>? = null
    private var investmentItemName: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_details, container, false)
        itemName = view.findViewById(R.id.item_name)
        itemPicker = view.findViewById(R.id.item_picker)
        itemPicker!!.setOrientation(DSVOrientation.HORIZONTAL)
        itemPicker!!.addOnItemChangedListener(this)
        itemName!!.text = investmentItemName
        infiniteScrollAdapter =
            InfiniteScrollAdapter.wrap(InvestmentCardAdapter(requireContext(), investments))
        itemPicker!!.adapter = infiniteScrollAdapter
        itemPicker!!.setItemTransformer(
            ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build()
        )
        return view
    }

    override fun onCurrentItemChanged(
        viewHolder: InvestmentCardAdapter.ViewHolder?,
        adapterPosition: Int) {
    }

    init {
        investmentItemName = itemName
    }
}