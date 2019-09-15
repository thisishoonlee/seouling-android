package mobile.seouling.com.application.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mobile.seouling.com.application.data.vo.Spot

class Converters {

    @TypeConverter
    fun toSpotList(value: String): List<Spot> = Gson().fromJson(value, object : TypeToken<List<Spot>>() {}.type)

    @TypeConverter
    fun fromSpotList(list: List<Spot>): String = Gson().toJson(list)

    @TypeConverter
    fun toStringList(value: String): List<String> = Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)

    @TypeConverter
    fun fromStringList(list: List<String>): String = Gson().toJson(list)
}
