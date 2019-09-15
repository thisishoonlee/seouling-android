package mobile.seouling.com.application.data.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName="comments")
data class Comment (
        @PrimaryKey @SerializedName("id") val id: Int,
        @SerializedName("author_name") val author: String

)