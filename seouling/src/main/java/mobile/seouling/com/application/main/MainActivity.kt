package mobile.seouling.com.application.main

import android.content.Intent
import android.os.Bundle
import mobile.seouling.com.BaseConstant
import mobile.seouling.com.application.common.BaseActivity
import mobile.seouling.com.R
import mobile.seouling.com.application.common.fragment.BaseFragmentType
import mobile.seouling.com.application.data_provider.BasePreference
import mobile.seouling.com.application.sign.SplashActivity
import mobile.seouling.com.framework.BaseEventBus
import mobile.seouling.com.framework.Log

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initIntent = preprocessIntent(intent)

        if (Intent.ACTION_MAIN == intent.action) {
            Intent(this, SplashActivity::class.java).let {
                startActivity(it)
                finish()
            }
        }

        setContentView(R.layout.activity_main)

        addFragment(BaseFragmentType.HOME, Bundle(), null)
    }

    override fun onStart() {
        super.onStart()
        BaseEventBus.getInstance().register(this)
    }

    override fun onStop() {
        super.onStop()
        BaseEventBus.getInstance().unregister(this)
    }

    private fun preprocessIntent(intent: Intent) {
        val newIntent = intent
        if (!isSignedIn()) {
            newIntent.putExtra(BaseConstant.BundleKey.EXTRA_INIT_FAIL, "init_fail")
        }
        setIntent(newIntent)
    }

    private fun isSignedIn(): Boolean {
        return BasePreference.isSignedIn()
    }
}
