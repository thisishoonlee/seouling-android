//package com.mallang.mobile.application.data.db
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.room.TypeConverters
//import androidx.sqlite.db.SupportSQLiteDatabase
//import com.mallang.mobile.BuildConfig
//import com.mallang.mobile.application.data.vo.IdealTypeConverter
//import com.mallang.mobile.application.data.vo.UsageReasonConverter
//import com.mallang.mobile.application.data.vo.User
//
//@Database(entities = [User::class, User.UserProfile::class], version = 1)
//@TypeConverters(value = arrayOf(IdealTypeConverter::class, UsageReasonConverter::class))
//abstract class AppDatabase : RoomDatabase() {
//    companion object {
//        private val IS_CLEAR_ALL = java.lang.Boolean.parseBoolean("true")
//        private const val DB_NAME = "mallang_db"
//
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
//            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
//        }
//
//        private fun buildDatabase(context: Context): AppDatabase =
//            Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
//                .apply {
//                    if (BuildConfig.DEBUG && IS_CLEAR_ALL) {
//                        addCallback(CALLBACK_CLEAR_ALL)
//                    }
//                }
//                .build()
//
//        private val CALLBACK_CLEAR_ALL = object : Callback() {
//            override fun onOpen(db: SupportSQLiteDatabase) {
//                super.onOpen(db)
//                db.execSQL("DELETE FROM users")
//            }
//        }
//    }
//
//    abstract fun userDao(): UserDao
//}