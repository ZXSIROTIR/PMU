package com.ip312.labs

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class Rules : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.rules,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val textRules =
            view.findViewById<TextView>(R.id.textRules)

        val html = resources
            .openRawResource(R.raw.game_rules)
            .bufferedReader()
            .use { it.readText() }

        textRules.text = Html.fromHtml(
            html,
            Html.FROM_HTML_MODE_LEGACY
        )
    }
}