package mobile.seouling.com.application.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import com.google.android.material.tabs.TabLayout
import mobile.seouling.com.BaseConstant
import mobile.seouling.com.R
import mobile.seouling.com.application.common.AbsFrameFragment
import mobile.seouling.com.application.common.IAppBarExpandable
import mobile.seouling.com.application.common.fragment.BaseFragment
import mobile.seouling.com.application.home.plan.HomePlanFragment
import mobile.seouling.com.framework.BaseEventBus
import mobile.seouling.com.framework.util.DipUnit

class HomeFrameFragment : AbsFrameFragment() {

    companion object {
        const val PAGE_HOME = 0
        const val PAGE_SEARCH = 1
        const val PAGE_MY_SEOUL = 2
        const val PAGE_PROFILE = 3
        const val PAGE_COUNT = 4
    }

    private val firstPage by lazy { arguments?.getInt(BaseConstant.BundleKey.EXTRA_FIRST_PAGE) ?: PAGE_HOME }
    private val isNewUser by lazy { arguments?.getBoolean(BaseConstant.BundleKey.EXTRA_IS_NEW_USER) ?: false }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_home_frame, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setElevation(tabs, DipUnit.toPixels(8f).toFloat())
    }

    override val initialPagePosition: Int
        get() = firstPage

    override fun onTabSelected(tab: TabLayout.Tab) {
        super.onTabSelected(tab)
        val position = tab.position
        val arguments = arguments
        arguments?.putInt(BaseConstant.BundleKey.EXTRA_HOME_PAGE_POSITION, position)
        val frag = currentChildFragment
        if (frag is IAppBarExpandable) {
            (frag as IAppBarExpandable).setExpanded(false)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        super.onTabReselected(tab)
        val frag = currentChildFragment
        if (frag is IAppBarExpandable) {
            (frag as IAppBarExpandable).setExpanded(true)
        }
    }

    override fun getFabIconId(position: Int): Int {
        return if (position == 0) {
            R.drawable.ic_plus
        } else {
            0
        }
    }

    override var pageCount: Int = PAGE_COUNT

    override fun onStart() {
        super.onStart()
        BaseEventBus.getInstance().register(this)
    }

    override fun onStop() {
        BaseEventBus.getInstance().unregister(this)
        super.onStop()
    }

    override fun onBackPressed(): Boolean {
        if (tabPosition != PAGE_HOME) {
            tabPosition = PAGE_HOME
            return true
        }
        return super.onBackPressed()
    }

    override fun updateTab(tab: TabLayout.Tab) {
        val customView = tab.customView
        val imageView: ImageView
        if (customView is ImageView) {
            imageView = customView
        } else {
            imageView = ImageView(context)
        }
        imageView.setImageDrawable(getTabIconDrawable(tab.position))
        tab.customView = imageView
    }

    private fun getTabIconDrawable(position: Int): Drawable {
        return when (position) {
            PAGE_HOME -> return resources.getDrawable(R.drawable.ic_maintabhome)
            PAGE_SEARCH -> return resources.getDrawable(R.drawable.ic_maintabsearch)
            PAGE_MY_SEOUL -> return resources.getDrawable(R.drawable.ic_maintabmyseoul)
            PAGE_PROFILE -> return resources.getDrawable(R.drawable.ic_maintabprofile)
            else -> resources.getDrawable(R.drawable.ic_maintabprofile)
        }
    }

    override fun createFragment(position: Int): BaseFragment {
        return when (position){
            PAGE_HOME -> HomePlanFragment()
            PAGE_SEARCH -> HomeSearchFragment()
            PAGE_MY_SEOUL -> HomeMySeoulFragment()
            PAGE_PROFILE -> HomeProfileFragment()
            else -> throw IllegalStateException("Invalid fragment position $position")
        }
    }
}