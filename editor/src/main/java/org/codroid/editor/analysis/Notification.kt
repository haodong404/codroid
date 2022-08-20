package org.codroid.editor.analysis

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.codroid.editor.R

const val GrammarAnalysisChannelId = "GrammarAnalysisChannel"

fun createAnalysisChannel(ctx: Context): NotificationChannelCompat {
    return NotificationChannelCompat.Builder(
        GrammarAnalysisChannelId,
        NotificationManagerCompat.IMPORTANCE_HIGH
    )
        .setName(ctx.getText(R.string.grammar_analysis_channel))
        .setDescription(ctx.getText(R.string.grammar_analysis_channel_desc).toString())
        .build()
        .also {
            NotificationManagerCompat.from(ctx).createNotificationChannel(it)
        }
}

fun analysisNotificationBuilder(ctx: Context): NotificationCompat.Builder =
    NotificationCompat.Builder(ctx, GrammarAnalysisChannelId)
        .setContentTitle(ctx.getText(R.string.grammar_analysing))
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setSmallIcon(R.drawable.ic_baseline_sync_24)