package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    fun formatRelativeTime(targetTime: Long): String {
        val now = System.currentTimeMillis()
        val diffMs = targetTime - now

        if (diffMs < -TimeUnit.MINUTES.toMillis(1)) {
            // Overdue
            val overdueMinutes = TimeUnit.MILLISECONDS.toMinutes(-diffMs)
            return if (overdueMinutes < 60) {
                "$overdueMinutes دقیقه تأخیر"
            } else {
                val hours = overdueMinutes / 60
                val mins = overdueMinutes % 60
                if (mins > 0) "$hours ساعت و $mins دقیقه تأخیر" else "$hours ساعت تأخیر"
            }
        } else if (Math.abs(diffMs) <= TimeUnit.MINUTES.toMillis(1)) {
            return "هم‌اکنون (موعد تزریق)"
        } else {
            // Upcoming
            val remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
            return if (remainingMinutes < 60) {
                "$remainingMinutes دقیقه دیگر"
            } else {
                val hours = remainingMinutes / 60
                val mins = remainingMinutes % 60
                if (mins > 0) "$hours ساعت و $mins دقیقه دیگر" else "$hours ساعت دیگر"
            }
        }
    }

    fun getDueStatus(targetTime: Long): DueStatus {
        val now = System.currentTimeMillis()
        val diffMs = targetTime - now

        return when {
            diffMs < -TimeUnit.MINUTES.toMillis(15) -> DueStatus.OVERDUE
            diffMs <= TimeUnit.MINUTES.toMillis(15) -> DueStatus.DUE_NOW
            diffMs <= TimeUnit.HOURS.toMillis(2) -> DueStatus.UPCOMING_SOON
            else -> DueStatus.SCHEDULED_LATER
        }
    }

    fun calculateNextDueTime(lastOrStartTime: Long, intervalHours: Int): Long {
        val intervalMs = TimeUnit.HOURS.toMillis(intervalHours.toLong())
        val now = System.currentTimeMillis()
        
        if (lastOrStartTime > now) {
            return lastOrStartTime
        }
        
        // If it was in the past, add interval from now or from last time
        var next = lastOrStartTime + intervalMs
        while (next < now - TimeUnit.MINUTES.toMillis(30) && intervalHours > 0) {
            next += intervalMs
        }
        return next
    }

    fun formatIntervalText(intervalHours: Int): String {
        return when (intervalHours) {
            1 -> "هر ۱ ساعت"
            2 -> "هر ۲ ساعت"
            4 -> "هر ۴ ساعت (Q4H)"
            6 -> "هر ۶ ساعت (Q6H)"
            8 -> "هر ۸ ساعت (TDS / Q8H)"
            12 -> "هر ۱۲ ساعت (BD / Q12H)"
            24 -> "روزی یک بار (هر ۲۴ ساعت)"
            36 -> "هر ۳۶ ساعت"
            48 -> "هر ۲ روز (هر ۴۸ ساعت)"
            72 -> "هر ۳ روز (هر ۷۲ ساعت)"
            168 -> "هفتگی (هر ۷ روز)"
            else -> "هر $intervalHours ساعت"
        }
    }
}

enum class DueStatus {
    OVERDUE,
    DUE_NOW,
    UPCOMING_SOON,
    SCHEDULED_LATER
}
