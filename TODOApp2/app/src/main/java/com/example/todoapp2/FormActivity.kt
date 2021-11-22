package com.example.todoapp2

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.todoapp2.MainActivity.Companion.NEW_TASK
import com.example.todoapp2.MainActivity.Companion.NEW_TASK_KEY
import com.example.todoapp2.MainActivity.Companion.UPDATE_TASK
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class FormActivity : AppCompatActivity() {

    private lateinit var edtTime: EditText
    private lateinit var edtTitle: EditText
    private lateinit var edtDate: EditText
    private lateinit var edtDescription: EditText
    private lateinit var btnAdd: Button

    private var isDetailTask = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        isDetailTask = intent.getBooleanExtra("isTaskDetail",false)

        initViews()
        if (isDetailTask) setTaskinfo(intent.getParcelableExtra("task")?: Task())
    }

    private fun setTaskinfo(task: Task) {

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


            val datePicker =  DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    edtDate.setText("${if(dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth}/${if(month < 10) {"0${month+1}"} else { month+1} }/$year")
                },
                nowDate.year,
                nowDate.monthValue - 1,
                nowDate.dayOfMonth
            )
            datePicker.datePicker.minDate = System.currentTimeMillis()
            datePicker.show()

        }

        edtTime.setOnClickListener {
            val nowTime = LocalTime.now()

            TimePickerDialog(this,
                { _, hour, minute ->
                    val h = if(hour<10) "0$hour"
                    else hour.toString()
                    val m = if(minute<10) "0$minute"
                    else minute.toString()
                    edtTime.setText("$h:$m")
                },
                nowTime.hour,
                nowTime.minute,
                true
            ).show()
        }

        btnAdd.setOnClickListener {

            if (edtTitle.text.isNotEmpty() && edtDate.text.isNotEmpty() && edtDescription.text.isNotEmpty() && edtTime.text.isNotEmpty()){
                setResult(
                    if (isDetailTask) UPDATE_TASK else NEW_TASK,
                    Intent().putExtra(
                        NEW_TASK_KEY,
                        Task(
                            intent.getParcelableExtra<Task>("task")?.id ?: 0,
                            edtTitle.text.toString(),
                            edtDescription.text.toString(),
                            LocalDateTime.of(LocalDate.parse(edtDate.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                LocalTime.parse(edtTime.text, DateTimeFormatter.ofPattern("HH:mm")))
                        )
                    ))
                finish()
            }else{
                finish()
            }
        }
    }
}