package mobile.seouling.com

import android.os.Bundle
import mobile.seouling.com.application.common.BaseActivity
import mobile.seouling.com.R

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
