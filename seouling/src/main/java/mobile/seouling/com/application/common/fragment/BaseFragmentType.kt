package mobile.seouling.com.application.common.fragment

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import mobile.seouling.com.application.home.HomeFrameFragment
import mobile.seouling.com.application.plan.PlanDetailFragment
import mobile.seouling.com.application.sign.LoginFragment
import mobile.seouling.com.application.sign.SignUpFragment

enum class BaseFragmentType constructor(private val mFragmentClass: Class<out BaseFragment>) {

    HOME(HomeFrameFragment::class.java),
    LOGIN(LoginFragment::class.java),
    SIGN_UP(SignUpFragment::class.java),
    PLAN_DETAIL(PlanDetailFragment::class.java);

    /**
     * Instantiates a VingleFragment with a given arguments
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    @NonNull
    fun createFragment(args: Bundle?, extraArgs: Map<String, Any>?): BaseFragment {
        try {
            val f = mFragmentClass.newInstance()
            f.arguments = args
            f.extraArguments = extraArgs
            return f
        } catch (e: InstantiationException) {
            e.printStackTrace()
            throw IllegalStateException(e)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            throw IllegalStateException(e)
        }

    }

    companion object {
        /**
         * Gets the FragmentType for the specified Fragment
         */
        fun getType(fragment: Fragment?): BaseFragmentType? {
            if (fragment == null) {
                return null
            }
            val cls = fragment.javaClass
            for (type in BaseFragmentType.values()) {
                if (cls == type.mFragmentClass) {
                    return type
                }
            }
            return null
        }
    }
}