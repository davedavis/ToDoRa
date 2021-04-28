package io.davedavis.todora.utils

import java.util.*

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS


//https://stackoverflow.com/questions/35858608/how-to-convert-time-to-time-ago-in-android
private fun currentDate(): Date {
    val calendar = Calendar.getInstance()
    return calendar.time
}

fun getTimeAgo(date: Date): String {
    var time = date.time
    if (time < 1000000000000L) {
        time *= 1000
    }

    val now = currentDate().time
    if (time > now || time <= 0) {
        return "in the future"
    }

    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> "Just now"
        diff < 2 * MINUTE_MILLIS -> "1 Min ago"
        diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} Mins ago"
        diff < 2 * HOUR_MILLIS -> "1 Hr Ago"
        diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} Hrs ago"
        diff < 48 * HOUR_MILLIS -> "yesterday"
        else -> "${diff / DAY_MILLIS} days ago"
    }
}