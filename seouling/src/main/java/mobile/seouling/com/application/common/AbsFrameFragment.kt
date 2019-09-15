package mobile.seouling.com.application.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import mobile.seouling.com.R
import mobile.seouling.com.application.common.fragment.BaseFragment
import mobile.seouling.com.framework.animation.FloatingActionButtonAnimator
import mobile.seouling.com.framework.util.KeyboardUtils

abstract class AbsFrameFragment : BaseFragment(), BaseFragmentContainer, OnTabSelectedListener {

    protected lateinit var tabs: TabLayout
    private var fabStub: ViewStub? = null

    private var fab: FloatingActionButton? = null
    private var fabAnimator: FloatingActionButtonAnimator? = null
    private var fabBehavior: QuickHideBehavior? = null

    protected abstract val initialPagePosition: Int

    protected var tabPosition: Int = 0
        set(value) {
            field = value
            if (!::tabs.isInitialized) return

            val tab = tabs.getTabAt(value)
            if (tab?.isSelected == false) {
                tab.select()
            }
            showFragment(value)
        }

    protected open var pageCount: Int = 0
        set(value) {
            field = value
            invalidateTabs()
        }

    private val fragments = mutableMapOf<Int, BaseFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabPosition = initialPagePosition
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_frame, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fabStub = view.findViewById(R.id.fab_stub)
        tabs = view.findViewById(R.id.tabs)
        tabs.addOnTabSelectedListener(this)
        invalidateTabs()
        showFragment(tabPosition)
    }

    protected fun invalidateTabs() {
        if (!::tabs.isInitialized) return
        tabs.run {
            val oldPosition = tabPosition
            val newPosition = if (oldPosition == -1) initialPagePosition else oldPosition
            removeAllTabs()
            repeat(pageCount) { i ->
                val newTab = newTab()
                addTab(newTab, i, i == newPosition)
                updateTab(newTab)
            }
        }
    }

    abstract fun updateTab(tab: TabLayout.Tab)

    protected fun inflateFab() {
        val stub = fabStub ?: return
        fab = stub.inflate() as FloatingActionButton
        fabAnimator = FloatingActionButtonAnimator(fab)
        val lp = fab?.layoutParams as CoordinatorLayout.LayoutParams
        fabBehavior = QuickHideBehavior()
        lp.behavior = fabBehavior
        setupFabIcon(initialPagePosition)
    }

    override fun onResume() {
        super.onResume()
        if (isVisibleToUser) {
            // Without this, title is not set on first launch.
            val toolbar = toolbar
            if (toolbar != null) {
                onDisplayTitle(toolbar)
            }
        }
    }

    override fun onDestroyView() {
        fab = null
        fabAnimator?.destroy()
        fabAnimator = null
        super.onDestroyView()
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        val frag = fragments[tab.position]
        if (frag is IScrollableToTop && frag.isAdded) {
            frag.scrollToTop()
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        tab.position.let {
            tabPosition = it
            setupFabIcon(it)
        }
        KeyboardUtils.hide(view)
    }

    private fun showFragment(position: Int) {
        val newFrag = getFragment(position)
        val oldFrag = childFragmentManager.findFragmentById(R.id.frame_fragment_container)
        if (oldFrag == null || oldFrag != newFrag) {
            childFragmentManager.beginTransaction()
                    .apply {
                        if (oldFrag == null) {
                            replace(R.id.frame_fragment_container, newFrag)
                        } else {
                            detach(oldFrag)
                            if (newFrag.isDetached) {
                                attach(newFrag)
                            } else {
                                add(R.id.frame_fragment_container, newFrag)
                            }
                        }
                    }
                    .commitNowAllowingStateLoss()
        }
    }

    protected fun getFragment(position: Int): BaseFragment {
        return fragments.getOrPut(position) { createFragment(position) }
    }

    override fun onDetach() {
        super.onDetach()
        fragments.clear()
    }

    private fun setupFabIcon(position: Int) {
        val icon = getFabIconId(position)
        fabAnimator?.setFabIcon(icon) {
            val frag = fragments[position]
            if (frag is OnFabClickListener) {
                frag.onFabClick(it)
            }
        }
    }

    override fun getCurrentChildFragment(): BaseFragment? {
        return childFragmentManager.findFragmentById(R.id.frame_fragment_container) as? BaseFragment
    }

    protected abstract fun createFragment(position: Int): BaseFragment

    protected open fun getFabIconId(position: Int): Int {
        return 0
    }
}
