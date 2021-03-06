package com.soumikshah.investmenttracker.view.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.soumikshah.investmenttracker.R
import com.soumikshah.investmenttracker.database.InvestmentHelper
import android.app.PendingIntent
import com.soumikshah.investmenttracker.view.MainActivity
import android.content.Intent
import android.content.SharedPreferences
import java.text.NumberFormat

/**
 * Implementation of App Widget functionality.
 */
class TotalAmountInvestedWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int) {
    val widgetText = context.getString(R.string.total_amount_invested)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.widget_total_amount_invested)
    views.setTextViewText(R.id.appwidget_text, widgetText)
    val investmentHelper = InvestmentHelper(context)
    val pref:SharedPreferences = context.getSharedPreferences("currency_name",0)

    val firstCurrency: String? = pref.getString("currency", "")
    val secondCurrency:String? = pref.getString("currency2","")
    var firstTotalAmount:String = String.format(NumberFormat.getInstance().
    format(investmentHelper.investmentTotalAmountWithCurrency(firstCurrency!!)))
    var secondTotalAmount:String = String.format(NumberFormat.getInstance().
    format(investmentHelper.investmentTotalAmountWithCurrency(secondCurrency!!)))
    firstTotalAmount = if(firstTotalAmount == "0"){
        ""
    } else{
        "$firstCurrency: $firstTotalAmount"
    }
    secondTotalAmount = if(secondTotalAmount == "0"){
        ""
    } else{
        "$secondCurrency: $secondTotalAmount"
    }
    val totalAmount = "$firstTotalAmount \n$secondTotalAmount"
    views.setTextViewText(R.id.total_amount_invested,totalAmount)

    //Implementing click function for widget to open the application.
    val configIntent = Intent(context, MainActivity::class.java)
    val configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0)
    views.setOnClickPendingIntent(R.id.parent_widget, configPendingIntent)
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}