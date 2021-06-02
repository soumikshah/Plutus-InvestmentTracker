package com.soumikshah.investmenttracker.view

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.model.Investment
import java.text.SimpleDateFormat
import java.util.*

class InvestmentAdapter
/**
 * Formatting timestamp to `MMM d` format
 * Input: 2018-02-21 00:15:42
 * Output: Feb 21
 */
/*
      private String formatDate(String dateStr) {
          try {
              SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
              Date date = fmt.parse(dateStr);
              SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d",Locale.ENGLISH);
              return fmtOut.format(date);
          } catch (ParseException e) {
              e.printStackTrace();
          }
  
          return "";
      }*/(private val context: Context, private val InvestmentsList: ArrayList<Investment>) : RecyclerView.Adapter<InvestmentAdapter.MyViewHolder>() {
    var mExpandedPosition = -1

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var investmentName: TextView = view.findViewById(R.id.investment)
        var investmentAmount: TextView = view.findViewById(R.id.investmentAmount)
        var investmentMedium = view.findViewById<TextView>(R.id.investmentMedium)
        var interestToBePaid: TextView = view.findViewById(R.id.interestToBePaid)
        var investmentCategory: TextView = view.findViewById(R.id.investmentCategory)
        var investmentDate: TextView = view.findViewById(R.id.investedDate)
        var investmentMonth: TextView = view.findViewById(R.id.investedNumberOfMonths)
        var viewBackground: RelativeLayout = view.findViewById(R.id.view_background)

        @JvmField
        var viewForeground: RelativeLayout = view.findViewById(R.id.view_foreground)
        var otherDetails: LinearLayout = view.findViewById(R.id.otherDetails)
        var viewBackgroundText: TextView = view.findViewById(R.id.background_text)
        var viewBackgroundImage: ImageView = view.findViewById(R.id.background_image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.investment_list_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val investment = InvestmentsList.get(position)
        holder.investmentName.text = investment.investmentName
        holder.investmentAmount.text = String.format(Locale.ENGLISH, "Rs.%,d", investment.investmentAmount)
        val investmentMedium = "Invested in: <b>" + investment.investmentMedium + "</b>"
        val sim = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val investmentCategory = "Investment Type: <b>" + investment.investmentCategory + "</b>"
        val investmentDate = "Invested on: <b>" + sim.format(investment.investmentDate) + "</b>"
        val investmentMonth = "Invested for: <b>" + investment.investmentMonth + "</b>"
        val investmentInterestToBePaid = "Interest to be paid: <b>" + investment.investmentPercent + "</b>"
        holder.investmentMedium.text = HtmlCompat.fromHtml(investmentMedium,HtmlCompat.FROM_HTML_MODE_LEGACY)
        holder.investmentCategory.text = HtmlCompat.fromHtml(investmentCategory,HtmlCompat.FROM_HTML_MODE_LEGACY)
        holder.investmentDate.text = HtmlCompat.fromHtml(investmentDate,HtmlCompat.FROM_HTML_MODE_LEGACY)
        holder.investmentMonth.text = HtmlCompat.fromHtml(investmentMonth,HtmlCompat.FROM_HTML_MODE_LEGACY)
        holder.interestToBePaid.text = HtmlCompat.fromHtml(investmentInterestToBePaid,HtmlCompat.FROM_HTML_MODE_LEGACY)
        if (investment.investmentPercent.toDouble() == 0.0) {
            holder.interestToBePaid.visibility = View.GONE
        } else {
            holder.interestToBePaid.visibility = View.VISIBLE
        }
        if (investment.investmentMonth == 0) {
            holder.investmentMonth.visibility = View.GONE
        } else {
            holder.investmentMonth.visibility = View.VISIBLE
        }
        if (investment.investmentCategory.isEmpty()) {
            holder.investmentCategory.visibility = View.GONE
        } else {
            holder.investmentCategory.visibility = View.VISIBLE
        }
        if (investment.investmentDate == 0L) {
            holder.investmentDate.visibility = View.GONE
        } else {
            holder.investmentDate.visibility = View.VISIBLE
        }
        val isExpanded = position == mExpandedPosition
        holder.otherDetails.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded
        holder.itemView.setOnClickListener {
            mExpandedPosition = if (isExpanded) -1 else position
            if (investment.investmentPercent.toDouble() == 0.0 && investment.investmentMonth == 0 && investment.investmentCategory.isEmpty() && investment.investmentDate == 0L) {
                Toast.makeText(holder.itemView.context, R.string.toast_message_no_detail, Toast.LENGTH_LONG).show()
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return InvestmentsList.size
    }

    fun removeItem(position: Int) {
        InvestmentsList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Investment, position: Int) {
        InvestmentsList.add(position, item)
        notifyItemInserted(position)
    } /*  */
}