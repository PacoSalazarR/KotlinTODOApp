package com.example.todoapp2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.work.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object{
        val NEW_TASK = 200
        val NEW_TASK_KEY = "newTask"
        val UPDATE_TASK = 201
    }

    private lateinit var rcvTask: RecyclerView
    private lateinit var btnAddTask: FloatingActionButton

    private val SAVED_TASKS = "task"

    private var isDetailTask = false
    private lateinit var adapter: TasksAdapter
    private var tasks = mutableListOf<Task>()

    private lateinit var db:TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            val savedTasks = it.getParcelableArrayList<Task>(SAVED_TASKS)?.toMutableList() ?: tasks
            tasks  = savedTasks
        }
        createNotificationChannel()
        initViews()
        isDetailTask = intent.getBooleanExtra("isTaskDetail",false)
        val task = intent.getParcelableExtra("task")?: Task()
        if (isDetailTask) {
            val intent = Intent(this, FormActivity::class.java).apply {
                putExtra("isTaskDetail",true)
                putExtra("task",task)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        db = Room.databaseBuilder(this, TaskDatabase::class.java, "Tasks").build()

        MainScope().launch {
            tasks = db.taskDao().getPendingTasks().toMutableList()

            setAdapter()
        }
    }

    private fun initViews(){
        rcvTask = findViewById(R.id.rcvTasks)
        btnAddTask = findViewById(R.id.btnAddTask)

        btnAddTask.setOnClickListener{

            startActivityForResult(Intent(this, FormActivity::class.java), NEW_TASK)
        }
    }

    private fun setAdapter(){
        adapter = TasksAdapter(tasks,onClickDoneTask = {task, position ->
            MainScope().launch {
                db.taskDao().updateTask(task.apply {
                    status = false
                })
                adapter.removeTask(position)
            }
        },onClickDetailTask ={ task ->
            startActivityForResult(Intent(this, FormActivity::class.java).apply {
                putExtra("isTaskDetail",true)
                putExtra("task",task)
            }, UPDATE_TASK)
        })

        rcvTask.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rcvTask.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.apply {
            putParcelableArrayList(SAVED_TASKS, tasks as ArrayList<Task>)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == NEW_TASK){
            data?.getParcelableExtra<Task>(NEW_TASK_KEY)?.let{

                MainScope().launch(Dispatchers.Main){
                    adapter.add(it)
                }

                MainScope().launch(Dispatchers.IO) {
                    db.taskDao().saveNewTask(it)

                    val zone = OffsetDateTime.now().offset
                    val selectedMillis = it.dateTime?.toInstant(zone)?.toEpochMilli() ?: 0
                    val nowMillis = LocalDateTime.now().toInstant(zone).toEpochMilli()

                    scheduleNotification(selectedMillis - nowMillis, Data.Builder().apply{
                        putInt("notificationID",it.id)
                        putString("notificationTitle", it.title)
                        putString("notificationDescription", it.description)
                        putString("notificationDateTime",it.dateTime.toString())
                    }.build())
                }
            }
        } else if (requestCode == UPDATE_TASK){
            data?.getParcelableExtra<Task>(NEW_TASK_KEY)?.let {

                MainScope().launch(Dispatchers.Main) {
                    adapter.update(it)
                }

                MainScope().launch(Dispatchers.IO) {
                    db.taskDao().updateTask(it)

                }
            }
        }
    }

    private fun scheduleNotification(delay: Long,data: Data){

        val notificationWork = OneTimeWorkRequest.Builder(NotificationManagerImpl::class.java)
            .setInitialDelay(delay,TimeUnit.MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance(this)
        instanceWorkManager.beginUniqueWork(
            "NOTIFICATION_WORK ${data.getInt("notificationID",0)}",
            ExistingWorkPolicy.APPEND_OR_REPLACE, notificationWork
        ).enqueue()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Tasks"
            val descriptionText = "Channel of pending task"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("TASK_CHANNEL",name,importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}