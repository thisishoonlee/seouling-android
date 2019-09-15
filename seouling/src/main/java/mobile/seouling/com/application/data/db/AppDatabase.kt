package mobile.seouling.com.application.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import mobile.seouling.com.BuildConfig
import mobile.seouling.com.application.data.vo.Auth
import mobile.seouling.com.application.data.vo.Comment
import mobile.seouling.com.application.data.vo.Plan
import mobile.seouling.com.application.data.vo.Schedule
import mobile.seouling.com.application.data.vo.Spot
import mobile.seouling.com.application.data.vo.Tag

@Database(entities = [
    Auth::class,
    Comment::class,
    Plan::class,
    Schedule::class,
    Spot::class,
    Tag::class
], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private val IS_CLEAR_ALL = java.lang.Boolean.parseBoolean("true")
        private const val DB_NAME = "seouling_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context): AppDatabase =
                Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                        .apply {
                            if (BuildConfig.DEBUG && IS_CLEAR_ALL) {
                                addCallback(CALLBACK_CLEAR_ALL)
                            }
                        }
                        .build()

        private val CALLBACK_CLEAR_ALL = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.execSQL("DELETE FROM users")
            }
        }
    }

}