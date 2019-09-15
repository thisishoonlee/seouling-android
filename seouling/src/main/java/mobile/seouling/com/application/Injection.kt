package mobile.seouling.com.application

import mobile.seouling.com.application.api.AuthService
import mobile.seouling.com.framework.network.BaseRetrofit

object Injection {

    /* Service */
    private fun getAuthService() : AuthService = BaseRetrofit.create(AuthService::class.java)

    /* Remote DataSource */

    /* Local DataSource */

    /* Repository */

    /* ViewModel Factory */
}