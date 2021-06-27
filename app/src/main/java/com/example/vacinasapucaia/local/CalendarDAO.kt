package com.example.vacinasapucaia.local

import android.content.Context
import androidx.room.*

@Dao
interface CalendarDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCalendar(calendar: DatabaseEntities)

    @Query("SELECT * FROM calendar ORDER BY id DESC LIMIT 1")
    fun getLastInsertion(): DatabaseEntities

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
                "calendar"
            ).build()
        }
    }

    return INSTANCE
}