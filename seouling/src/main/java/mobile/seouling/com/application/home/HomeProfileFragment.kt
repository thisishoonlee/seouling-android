package mobile.seouling.com.application.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mobile.seouling.com.R
import mobile.seouling.com.application.common.fragment.BaseFragment

class HomeProfileFragment: BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_home_profile, container, false)
    }
}