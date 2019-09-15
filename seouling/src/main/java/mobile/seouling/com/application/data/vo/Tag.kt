package mobile.seouling.com.application.data.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tags")
data class Tag (
        @PrimaryKey @SerializedName("id") val id: Int,
        @SerializedName("bundle")val bundle: List<String>
)