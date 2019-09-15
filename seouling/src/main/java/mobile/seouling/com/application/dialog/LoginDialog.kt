package mobile.seouling.com.application.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.dialog_login.*
import mobile.seouling.com.R
import mobile.seouling.com.base_android.doOnClick


class LoginDialog(context: Context,
                  private val callback: LoginDialogCallback) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowManager.LayoutParams().apply {
            flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dimAmount = 0.8f
        }.let { window.attributes = it }

        setContentView(R.layout.dialog_login)

        login.doOnClick {
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()
            if (email.isNotBlank() &&
                    password.isNotBlank()) {
                callback.onLoginClick(email, password)
            }
        }
        close.doOnClick {
            dismiss()
        }
    }

    interface LoginDialogCallback {
        fun onLoginClick(email: String, password: String)
    }
}