package com.example.todoapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDateTime
import java.time.Month
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    companion object {
        val NEW_TASK = 200
        val NEW_TASK_KEY = "newTask"
    }

    private lateinit var rcvTasks: RecyclerView
    private lateinit var btnAddTask: FloatingActionButton

    private val SAVED_TASKS = "tasks"

    private lateinit var adapter: TasksAdapter
    private var tasks = mutableListOf(
        Task(0,"Test","Description", LocalDateTime.now()),
        Task(1,"Test1","Description1", LocalDateTime.of(2021, Month.AUGUST, 6, 12, 40)),
        Task(2,"Test2","Description2", LocalDateTime.of(2022, Month.AUGUST, 6, 12, 40)),
        Task(3,"Test3","Description3", LocalDateTime.of(2023, Month.AUGUST, 6, 12, 40))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            val savedTasks = it.getParcelableArrayList<Task>(SAVED_TASKS)?.toMutableList() ?: tasks
            tasks = savedTasks
        }

        initViews()
        setAdapter()
    }

    private fun initViews(){
        rcvTasks = findViewById(R.id.rcvTasks)
        btnAddTask = findViewById(R.id.btnAddTask)

        btnAddTask.setOnClickListener {
            //adapter.add()
            startActivityForResult(Intent(this, FormActivity::class.java), NEW_TASK)
        }
    }

    private fun setAdapter(){
        adapter = TasksAdapter(tasks)

        rcvTasks.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rcvTasks.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.apply {
            putParcelableArrayList(SAVED_TASKS, tasks as ArrayList<Task>)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == NEW_TASK) {
            data?.getParcelableExtra<Task>(NEW_TASK_KEY)?.let {
                adapter.add(it)
            }
        }
    }

}