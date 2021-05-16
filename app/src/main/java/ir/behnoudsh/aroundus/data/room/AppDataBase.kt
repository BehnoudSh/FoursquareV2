package ir.behnoudsh.aroundus.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FoursquarePlace::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun foursquareplacesDao(): FoursquarePlacesDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "aroundus_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }
}