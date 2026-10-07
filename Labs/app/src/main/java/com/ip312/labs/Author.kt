package com.ip312.labs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class Author : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.authorsview,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val listAuthors =
            view.findViewById<ListView>(R.id.listAuthors)

        val authors = listOf(
            Pair("Сироткин Игорь ИП-312", R.drawable.tvorogok),
            Pair("Сафронова Алиса ИП-312", R.drawable.sirnik),
        )

        val adapter = object : ArrayAdapter<Pair<String, Int>>(
            requireContext(),
            R.layout.author,
            authors
        ) {

            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {

                val view = convertView ?: LayoutInflater
                    .from(requireContext())
                    .inflate(
                        R.layout.author,
                        parent,
                        false
                    )

                val imageAuthor =
                    view.findViewById<ImageView>(
                        R.id.imageAuthor
                    )

                val textAuthorName =
                    view.findViewById<TextView>(
                        R.id.textAuthorName
                    )

                val author = getItem(position)!!

                textAuthorName.text = author.first
                imageAuthor.setImageResource(author.second)

                return view
            }
        }

        listAuthors.adapter = adapter
    }
}