package com.example.todoapp2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import java.time.format.DateTimeFormatter

class TasksAdapter(
    private val list: MutableList<Task>,
    var onClickDoneTask: (task: Task, position: Int) -> Unit,
    var onClickDetailTask: (task: Task) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun update(task: Task){
        val index = list.indexOfFirst { it.id == task.id }
        list[index] = task
        notifyItemChanged(index)
    }

    fun add(task: Task){
        list.add(task)
        notifyItemInserted(list.size-1)
    }

    fun remove(position: Int){
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TaskViewHolder).bind(list[position])
    }

    override fun getItemCount() = list.size

    inner class TaskViewHolder(private val view: View):RecyclerView.ViewHolder(view){

        fun bind(data: Task) = view.apply{
            val txvTitle = findViewById<TextView>(R.id.txvTitle)
            val txvDateTime = findViewById<TextView>(R.id.txtDateTime)
            val chkFinished = findViewById<MaterialCheckBox>(R.id.chkFinish)

            txvTitle.text = data.title
            txvDateTime.text = data.dateTime?.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm a"))

            chkFinished.isChecked = false

            chkFinished.setOnClickListener {
                onClickDoneTask(data, adapterPosition)
            }

            rootView.setOnClickListener {
                onClickDetailTask(data)
            }
        }

    }
}