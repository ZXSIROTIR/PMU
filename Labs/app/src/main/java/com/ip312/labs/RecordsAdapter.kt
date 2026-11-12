package com.ip312.labs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ip312.labs.data.RecordRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordsAdapter : RecyclerView.Adapter<RecordsAdapter.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val place: TextView = view.findViewById(R.id.textPlace)
        val name: TextView = view.findViewById(R.id.textName)
        val score: TextView = view.findViewById(R.id.textScore)
        val details: TextView = view.findViewById(R.id.textDetails)
    }

    private var items: List<RecordRow> = emptyList()

    private val dateFormat =
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    @SuppressLint("NotifyDataSetChanged")
    fun submit(list: List<RecordRow>) {
        items = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_record, parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val row = items[position]

        val totalTaps = row.hits + row.misses
        val accuracy =
            if (totalTaps == 0) 0
            else row.hits * 100 / totalTaps

        holder.place.text = "${position + 1}."
        holder.name.text = row.name
        holder.score.text = row.score.toString()

        holder.details.text =
            "Сложность: ${row.difficulty} · Точность: $accuracy% " +
                    "(попаданий ${row.hits}, промахов ${row.misses})\n" +
                    "Раунд: ${row.roundMinutes} мин. · " +
                    dateFormat.format(Date(row.playedAt))
    }
}
