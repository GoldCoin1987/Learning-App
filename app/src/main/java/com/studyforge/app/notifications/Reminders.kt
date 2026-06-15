package com.studyforge.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.studyforge.app.MainActivity
import com.studyforge.app.StudyForgeApp
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

const val CHANNEL_ID = "study_reminders"
private const val NOTIFICATION_ID = 1001

fun ensureNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        CHANNEL_ID,
        "Study reminders",
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply { description = "Daily reminder to review your due cards" }
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
}

/**
 * Daily reminder via a self-rescheduling one-time WorkManager job (reliable under Doze, unlike
 * pure-web notifications). Fires at [ReminderScheduler.reminderHour], shows the due count, then
 * re-enqueues itself for the next day.
 */
class ReminderWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as StudyForgeApp
        val today = LocalDate.now().toEpochDay()
        val due = app.container.db.itemDao().dueItemsScoped(today, null, null, 1000).size
        if (due > 0) showReminder(applicationContext, due)
        ReminderScheduler.scheduleNext(applicationContext)
        return Result.success()
    }
}

object ReminderScheduler {
    private const val WORK_NAME = "daily_study_reminder"
    var reminderHour: Int = 18 // 6 PM; wire to a settings screen later

    fun scheduleNext(context: Context) {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(reminderHour, 0)
        if (!next.isAfter(now)) next = next.plusDays(1)
        val delayMinutes = Duration.between(now, next).toMinutes().coerceAtLeast(1)

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }
}

private fun showReminder(context: Context, dueCount: Int) {
    val intent = Intent(context, MainActivity::class.java)
        .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
    val pending = PendingIntent.getActivity(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("Time to study")
        .setContentText("You have $dueCount card${if (dueCount == 1) "" else "s"} due for review.")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentIntent(pending)
        .setAutoCancel(true)
        .build()

    val manager = NotificationManagerCompat.from(context)
    if (manager.areNotificationsEnabled()) {
        try {
            manager.notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS not granted; silently skip.
        }
    }
}
