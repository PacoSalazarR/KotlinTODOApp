package com.example.todoapp2

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.widget.EditText
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todoapp2.LocalDateTimeConverter.toDateTime
import com.example.todoapp2.MainActivity.Companion.DETAIL_TASK_KEY
import com.example.todoapp2.MainActivity.Companion.TASK_KEY_EXTRA

class NotificationManagerImpl(private val context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val id = inputData.getInt("notificationID",0)
        val title = inputData.getString("notificationTitle") ?: ""
        val description = inputData.getString("notificationDescription") ?: ""
        val datetime = inputData.getString("notificationDateTime") ?: ""
        createNotification(Task(id,title,description,toDateTime(datetime)))

        return success()
    }

    private fun createNotification(task: Task){
        val resultIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(DETAIL_TASK_KEY,true)
            putExtra(TASK_KEY_EXTRA,task)
        }

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(context,"TASK_CHANNEL")
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle(task.title)
            .setContentText(task.description)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)

        with(NotificationManagerCompat.from(context)){
            notify(task.id,builder.build())
        }

    }

}