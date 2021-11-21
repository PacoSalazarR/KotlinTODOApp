package com.example.todoapp2

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import com.example.todoapp2.MainActivity.Companion.NEW_TASK
import com.example.todoapp2.MainActivity.Companion.NEW_TASK_KEY
import com.example.todoapp2.MainActivity.Companion.UPDATE_TASK
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class FormActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var edtDate: EditText
    private lateinit var edtTime: EditText
    private lateinit var btnAdd: Button

    private var isDetailTask = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        isDetailTask = intent.getBooleanExtra("isTaskDetail", false)

        initViews()
        if(isDetailTask) setTaskInfo(intent.getParcelableExtra("task")?:Task())
    }

    private fun setTaskInfo(task: Task) {
        edtTitle.setText(task.title)
        edtDescription.setText(task.description)
        edtDate.setText(task.dateTime?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        edtTime.setText(task.dateTime?.format(DateTimeFormatter.ofPattern("HH:mm")))

        btnAdd.text = "Update"
    }

    @SuppressLint("SetTextI18n")
    private fun initViews(){
        edtTitle = findViewById(R.id.edtTitle)
        edtDescription = findViewById(R.id.edtDescription)
        edtDate = findViewById(R.id.edtDate)
        edtTime = findViewById(R.id.edtTime)
        btnAdd = findViewById(R.id.btnAdd)

        edtDate.setOnClickListener {
            val nowDate = LocalDate.now()

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    edtDate.setText("$dayOfMonth/${month+1}/$year")
                },
                nowDate.year,
                nowDate.monthValue - 1,
                nowDate.dayOfMonth,

            )
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }

        edtTime.setOnClickListener {
            val nowTime = LocalTime.now()

            TimePickerDialog(
                this,
                { _, hour, minute ->
                    edtTime.setText("$hour:$minute")
                },
                nowTime.hour,
                nowTime.minute,
                true
            ).show()
        }

        btnAdd.setOnClickListener {
            if(edtTime.text.isNotEmpty()&&edtDate.text.isNotEmpty()&&edtTitle.text.isNotEmpty()&&edtDescription.text.isNotEmpty()){
                setResult(
                    if(isDetailTask) UPDATE_TASK else NEW_TASK,
                    Intent().putExtra(
                        NEW_TASK_KEY,
                        Task(
                            intent.getParcelableExtra<Task>("task")?.id ?: 0,
                            edtTitle.text.toString(),
                            edtDescription.text.toString(),
                            LocalDateTime.of(
                                LocalDate.parse(
                                    edtDate.text,
                                    DateTimeFormatter.ofPattern("d/M/yyyy")
                                ),
                                LocalTime.parse(edtTime.text, DateTimeFormatter.ofPattern("H:m"))
                            )
                        )
                    )
                )
                finish()
            }
        }
    }
}