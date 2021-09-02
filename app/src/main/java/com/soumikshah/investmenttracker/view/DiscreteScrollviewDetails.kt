package com.soumikshah.investmenttracker.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import com.soumikshah.investmenttracker.database.model.Investment
import com.yarolegovich.discretescrollview.DiscreteScrollView.OnItemChangedListener
import android.widget.TextView
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.soumikshah.investmenttracker.R
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import java.util.ArrayList

class DiscreteScrollviewDetails internal constructor(
    private val investments: ArrayList<Investment>,
    itemName: String?,
    itemId: Int?
) : Fragment(), OnItemChangedListener<CategoryScrollingCardView.ViewHolder?> {
    private var investment: Investment? = null
    private var itemName: TextView? = null
    private var itemPicker: DiscreteScrollView? = null
    private var infiniteScrollAdapter: InfiniteScrollAdapter<*>? = null
    private var deleteButton: Button? = null
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
        deleteButton = view.findViewById(R.id.delete_button)
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
        val shake: Animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.shake)
        deleteButton!!.startAnimation(shake)
        val targetPosition = infiniteScrollAdapter!!.getClosestPosition(positionForRecyclerView!!)
        itemPicker!!.scrollToPosition(targetPosition)
        itemPicker!!.setSlideOnFling(true)
        itemPicker!!.setItemTransformer(
            ScaleTransformer.Builder()
                .setMaxScale(1.0f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                .build()
        )

        deleteButton!!.setOnClickListener {
            investment = investments[infiniteScrollAdapter!!.realCurrentPosition]
            deleteDialog()
        }
        backButton!!.setOnClickListener { activity?.onBackPressed() }
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteDialog() {
        val context: Context = ContextThemeWrapper(requireContext(), R.style.DialogboxTheme)
        val builder:MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            .setMessage(getString(R.string.delete_investment_title))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                (activity as MainActivity).mainFragment!!.investmentHelper!!.deleteInvestment(
                    investment!!
                )
                investments.remove(investment)
                dialog.dismiss()
                infiniteScrollAdapter!!.notifyDataSetChanged()
                (activity as? MainActivity)!!.updateViewPager()
                activity?.onBackPressed()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
        val nbutton: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        nbutton.setBackgroundColor(Color.GREEN)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(40, 0, 0, 0)
        val pbutton: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        pbutton.setBackgroundColor(Color.RED)
        pbutton.layoutParams = params
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