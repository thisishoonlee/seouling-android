package mobile.seouling.com.application.data.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "auth")
data class Auth(
        @PrimaryKey @SerializedName("id") val id: Int,
        @SerializedName("sns_token") val snsToken: String?,
        @SerializedName("email") val email: String?,
        @SerializedName("password") val password: String,
        @SerializedName("nickname") val nickname: String,
        @SerializedName("profile_picture") val profile: String,
        @SerializedName("is_push") val isPush: Boolean,
        @SerializedName("login_type") val loginType: Int,
        @SerializedName("last_login") val lastLogin: String?,
        @SerializedName("created_at") val createdAt: String
)