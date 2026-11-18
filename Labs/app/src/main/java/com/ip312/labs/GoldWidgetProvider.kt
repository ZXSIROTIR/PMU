package com.ip312.labs

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.ip312.labs.gold.GoldRate
import com.ip312.labs.gold.GoldRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GoldWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val repository = GoldRepository.get(context)

        updateWidgets(context, appWidgetManager, appWidgetIds, repository.cached())

        val pending = goAsync()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val fresh = repository.refresh()

                if (fresh != null) {
                    updateWidgets(context, appWidgetManager, appWidgetIds, fresh)
                }
            } finally {
                pending.finish()
            }
        }
    }

    private fun updateWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        rate: GoldRate?
    ) {
        val views = buildViews(context, rate)

        for (id in appWidgetIds) {
            appWidgetManager.updateAppWidget(id, views)
        }
    }

    private fun buildViews(
        context: Context,
        rate: GoldRate?
    ): RemoteViews {

        val views = RemoteViews(context.packageName, R.layout.widget_gold)

        if (rate != null) {
            views.setTextViewText(R.id.widgetRate, rate.formattedRate())
            views.setTextViewText(R.id.widgetDate, "на ${rate.date}")
        } else {
            views.setTextViewText(R.id.widgetRate, "—")
            views.setTextViewText(R.id.widgetDate, "нет данных")
        }

        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.widgetRoot, pendingIntent)

        return views
    }
}
