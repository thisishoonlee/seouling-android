package mobile.seouling.com.application.sign

import android.os.Bundle
import mobile.seouling.com.R
import mobile.seouling.com.application.common.BaseActivity
import mobile.seouling.com.application.common.fragment.BaseFragmentType
import mobile.seouling.com.framework.BaseEventBus

class SignInProcessActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        addFragment(BaseFragmentType.LOGIN, Bundle(), null)
    }

    override fun onStart() {
        super.onStart()
        BaseEventBus.getInstance().register(this)
    }

    override fun onStop() {
        super.onStop()
        BaseEventBus.getInstance().unregister(this)
    }
}