package com.example.vacinasapucaia.local

import android.content.Context
import androidx.room.*
import com.example.vacinasapucaia.utils.DATABASE_ITEM_DESCRIPTION_CALENDAR

@Dao
interface CalendarDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCalendar(calendar: DatabaseEntities)

    @Query("SELECT * FROM calendar WHERE description ==:desc ORDER BY id DESC LIMIT 1")
    fun getLastInsertion(desc: String): DatabaseEntities

    @Query(" SELECT COUNT(*) FROM calendar ")
    fun getDatabaseSize(): Int

    @Query("DELETE FROM calendar")
    fun clearRoom()

}

@Database(entities = [DatabaseEntities::class], version = 1, exportSchema = false)
abstract class CalendarDatabase : RoomDatabase() {
    abstract val calendarDAO: CalendarDAO
}

private lateinit var INSTANCE: CalendarDatabase

fun getDatabase(context: Context): CalendarDatabase {

    synchronized(CalendarDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                CalendarDatabase::class.java,
                DATABASE_ITEM_DESCRIPTION_CALENDAR
            ).build()
        }
    }

    return INSTANCE
}