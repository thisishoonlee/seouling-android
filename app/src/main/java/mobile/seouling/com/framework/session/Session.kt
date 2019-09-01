package mobile.seouling.com.framework.session

import android.content.Intent

data class Session(
    val id: String,
    var state: SessionState,
    var intent: Intent?)
