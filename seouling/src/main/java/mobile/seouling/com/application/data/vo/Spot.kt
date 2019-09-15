package mobile.seouling.com.application.data.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "spots")
data class Spot(
        @PrimaryKey @SerializedName("spot_id") val id: Int,
        @SerializedName("scheme") val scheme: String,
        @SerializedName("picture") val picture: String,
        @SerializedName("name") val name: String
)