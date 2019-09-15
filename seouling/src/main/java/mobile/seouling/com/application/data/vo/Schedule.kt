package mobile.seouling.com.application.data.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "schedules")
data class Schedule(
        @PrimaryKey @SerializedName("id")val id: Int,
        @SerializedName("date")val date: String,
        @SerializedName("morning")val morning: List<Spot>,
        @SerializedName("after_noon")val afternoon: List<Spot>,
        @SerializedName("night")val night: List<Spot>
)