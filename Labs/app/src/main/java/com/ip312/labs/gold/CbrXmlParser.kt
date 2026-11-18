package com.ip312.labs.gold

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

object CbrXmlParser {

    private const val GOLD_CODE = "1"

    fun parseGold(input: InputStream): GoldRate? {

        val parser = Xml.newPullParser()

        parser.setInput(input, null)

        var result: GoldRate? = null

        var date: String? = null
        var isGold = false
        var buy: Double? = null
        var sell: Double? = null
        var tag = ""

        var event = parser.eventType

        while (event != XmlPullParser.END_DOCUMENT) {

            when (event) {

                XmlPullParser.START_TAG -> {
                    tag = parser.name

                    if (tag == "Record") {
                        date = parser.getAttributeValue(null, "Date")
                        isGold = parser.getAttributeValue(null, "Code") == GOLD_CODE
                        buy = null
                        sell = null
                    }
                }

                XmlPullParser.TEXT -> {
                    if (isGold) {
                        when (tag) {
                            "Buy" -> buy = parseNumber(parser.text)
                            "Sell" -> sell = parseNumber(parser.text)
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name == "Record" && isGold) {
                        val value = buy ?: sell

                        if (value != null && date != null) {
                            result = GoldRate(value, date)
                        }

                        isGold = false
                    }

                    tag = ""
                }
            }

            event = parser.next()
        }

        return result
    }

    fun parseNumber(text: String): Double? =
        text.replace("\u00A0", "")
            .replace(" ", "")
            .replace(',', '.')
            .toDoubleOrNull()
}
