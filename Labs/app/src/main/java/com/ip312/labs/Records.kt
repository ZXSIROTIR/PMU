package com.ip312.labs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ip312.labs.data.AppDatabase
import kotlinx.coroutines.launch

class Records : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.records,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val recycler =
            view.findViewById<RecyclerView>(R.id.recyclerRecords)

        val textEmpty =
            view.findViewById<TextView>(R.id.textEmpty)

        val adapter = RecordsAdapter()

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        recycler.adapter = adapter

        val dao = AppDatabase
            .getInstance(requireContext())
            .recordDao()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                dao.observeTop().collect { rows ->
                    adapter.submit(rows)
                    textEmpty.visibility =
                        if (rows.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }
}
