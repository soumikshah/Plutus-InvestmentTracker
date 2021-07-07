package com.soumikshah.investmenttracker.view

import com.soumikshah.investmenttracker.database.model.Investment
import com.yarolegovich.discretescrollview.DiscreteScrollView.OnItemChangedListener
import android.widget.TextView
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.soumikshah.investmenttracker.R
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import java.util.ArrayList

class DiscreteScrollviewDetails internal constructor(
    private val investments: ArrayList<Investment>,
    itemName: String?,
    itemId: Int?
) : Fragment(), OnItemChangedListener<CategoryScrollingCardView.ViewHolder?> {
    private val investment: Investment? = null
    private var itemName: TextView? = null
    private var itemPicker: DiscreteScrollView? = null
    private var infiniteScrollAdapter: InfiniteScrollAdapter<*>? = null
    private var investmentItemName: String? = null
    private var investmentItemId: Int? = 0
    private var positionForRecyclerView: Int? =0
    private var backButton: Button? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_discretescrollview_details, container, false)
        itemName = view.findViewById(R.id.item_name)
        itemPicker = view.findViewById(R.id.item_picker)
        backButton = view.findViewById(R.id.backButton)
        itemPicker!!.setOrientation(DSVOrientation.HORIZONTAL)
        itemPicker!!.addOnItemChangedListener(this)
        itemName!!.text = investmentItemName
        for((index,value) in investments.withIndex()){
            if(value.id == investmentItemId){
                positionForRecyclerView = index
            }
        }

        infiniteScrollAdapter =
            InfiniteScrollAdapter.wrap(CategoryScrollingCardView(requireContext(), investments))
        itemPicker!!.adapter = infiniteScrollAdapter

        val targetPosition = infiniteScrollAdapter!!.getClosestPosition(positionForRecyclerView!!)
        itemPicker!!.scrollToPosition(targetPosition)

        itemPicker!!.setItemTransformer(
            ScaleTransformer.Builder()
                .setMinScale(0.9f)
                .build()
        )

        backButton!!.setOnClickListener { activity?.onBackPressed() }
        return view
    }

    override fun onCurrentItemChanged(
        viewHolder: CategoryScrollingCardView.ViewHolder?,
        adapterPosition: Int) {
    }

    init {
        investmentItemName = itemName
        investmentItemId = itemId
    }
}