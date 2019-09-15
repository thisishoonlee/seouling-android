package mobile.seouling.com.application.sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.frag_login.*
import mobile.seouling.com.application.common.fragment.BaseFragment
import android.content.Intent
import android.widget.Toast
import mobile.seouling.com.application.common.fragment.BaseFragmentType
import mobile.seouling.com.application.dialog.LoginDialog
import mobile.seouling.com.application.main.MainActivity
import mobile.seouling.com.base_android.doOnClick


class LoginFragment : BaseFragment(), LoginDialog.LoginDialogCallback{

    private val callbackManager = CallbackManager.Factory.create()
    private val loginDialog: LoginDialog by lazy { LoginDialog(requireContext(), this)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(mobile.seouling.com.R.layout.frag_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        facebookLoginButton.apply {
            val permissions = mutableListOf<String>()
            permissions.add("email")
            setPermissions("email", "birth")
            fragment = this@LoginFragment
            registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    Toast.makeText(requireContext(), "Success!", Toast.LENGTH_SHORT).show()
                }

                override fun onCancel() {
                    Toast.makeText(requireContext(), "Cancel!", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show()
                }
            })
        }

        emailLogin.doOnClick{
            loginDialog.show()
        }
        emailSignUp.doOnClick{
            (activity as? SignInProcessActivity)?.let {
                it.addFragment(BaseFragmentType.SIGN_UP, Bundle(), null)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onLoginClick(email: String, password: String) {
        Intent(activity, MainActivity::class.java).let {
            startActivity(it)
            activity?.finish()
        }
    }
}