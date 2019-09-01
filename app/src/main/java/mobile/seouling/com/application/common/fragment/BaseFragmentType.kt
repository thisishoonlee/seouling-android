package mobile.seouling.com.application.common.fragment

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import mobile.seouling.com.application.home.HomeFragment

enum class BaseFragmentType private constructor(private val mFragmentClass: Class<out BaseFragment>) {

    HOME(HomeFragment::class.java);
//
//    SIGN_UP_CALL(SignUpCallFragment::class.java),
//    SIGN_UP_VERIFY_CALL(SignUpVerifyCallFragment::class.java),
//    SIGN_UP_FIRST_PROFILE(SignUpFirstProfileFragment::class.java),
//    SIGN_UP_SECOND_PROFILE(SignUpSecondProfileFragment::class.java),
//    SIGN_UP_UPLOAD_PROFILE(SignUpUploadProfileFragment::class.java);

    /**
     * Instantiates a VingleFragment with a given arguments
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    @NonNull
    fun createFragment(args: Bundle?, extraArgs: Map<String, Any>?): BaseFragment {
        try {
            val f = mFragmentClass.newInstance()
            f.setArguments(args)
            f.setExtraArguments(extraArgs)
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
        fun getType(mallangFrag: Fragment?): BaseFragmentType? {
            if (mallangFrag == null) {
                return null
            }
            val cls = mallangFrag.javaClass
            for (type in BaseFragmentType.values()) {
                if (cls == type.mFragmentClass) {
                    return type
                }
            }
            return null
        }
    }
}