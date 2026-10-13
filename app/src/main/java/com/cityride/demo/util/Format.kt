package com.cityride.demo.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Format {
    fun euro(cents: Int): String = String.format(Locale.ITALY, "%,.2f €", cents / 100.0)

    fun countdown(millis: Long): String {
        val totalSeconds = millis / 1000
        return String.format(
            Locale.ROOT, "%02d:%02d:%02d",
            totalSeconds / 3600, (totalSeconds % 3600) / 60, totalSeconds % 60,
        )
    }

    fun dateTime(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.ITALY).format(Date(millis))
}
