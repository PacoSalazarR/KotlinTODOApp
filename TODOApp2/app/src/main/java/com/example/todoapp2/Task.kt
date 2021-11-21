package com.example.todoapp2

import android.os.Parcelable
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Entity
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val dateTime: LocalDateTime? = null,
    var status: Boolean = true

): Parcelable

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task WHERE status = 1")
    suspend fun getPendingTasks(): List<Task>

    @Insert(onConflict = REPLACE)
    suspend fun saveNewTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

}

@Database(entities = [Task::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
abstract class TaskDatabase() : RoomDatabase(){
    abstract fun taskDao() : TaskDao
}

object LocalDateTimeConverter {

    @TypeConverter
    fun toDateTime(dateString: String?): LocalDateTime?{
        return if (dateString == null) null else LocalDateTime.parse(dateString)
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String?{
        return date?.toString()
    }

}