package mobile.seouling.com.application.data.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "plans")
data class Plan(
        @PrimaryKey @SerializedName("id") val id: Int,
        @SerializedName("scheme") val scheme: String,
        @SerializedName("name") val name: String,
        @SerializedName("picture") val picture: String,
        @SerializedName("created_date") val createdDate: String,
        @SerializedName("start_date") val start: String,
        @SerializedName("end_date") val end: String
)