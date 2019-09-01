package mobile.seouling.com.application.common

import android.annotation.TargetApi
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.app.TaskStackBuilder
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.squareup.otto.Subscribe
import mobile.seouling.com.BaseConstant
import mobile.seouling.com.R
import mobile.seouling.com.application.common.fragment.BaseFragment
import mobile.seouling.com.application.common.fragment.BaseFragmentType
import mobile.seouling.com.application.events.PermissionResponseEvent
import mobile.seouling.com.application.events.RequestPermissionEvent
import mobile.seouling.com.application.events.ShowFragmentEvent
import mobile.seouling.com.framework.BaseEventBus
import mobile.seouling.com.framework.DebugHelper
import mobile.seouling.com.framework.Log
import mobile.seouling.com.framework.activity_helper.CommonEventDispatcher
import mobile.seouling.com.framework.activity_helper.EventDispatcher
import mobile.seouling.com.framework.activity_helper.MaterialTransitions
import mobile.seouling.com.framework.rx.RxActivity
import java.io.PrintWriter
import java.io.StringWriter
import java.util.ArrayList

abstract class BaseActivity : RxActivity() {

    companion object {
        protected const val TAG: String = "MallangBaseActivity"
    }

    private val eventDispatchers = mutableListOf<EventDispatcher>()

    protected open fun isOpaqueActivity(): Boolean {
        return false
    }

    private val mEventSubscriber = object : Any() {
        @Suppress("unused")
        @Subscribe
        fun onRequestPermission(event: RequestPermissionEvent) {
            ActivityCompat.requestPermissions(
                this@BaseActivity, event.permissions,
                BaseConstant.Permission.REQUEST_CODE_FROM_EVENT
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate with intent: " + intent + ", fromSavedState:" + (savedInstanceState != null))
        super.onCreate(savedInstanceState)
        if (isOpaqueActivity()) {
            window.setBackgroundDrawable(null)
        }
        onCreateEventDispatchers(eventDispatchers)
        for (dispatcher in eventDispatchers) {
            dispatcher.onActivityCreate(this)
        }
    }

    protected open fun onCreateEventDispatchers(dispatchers: MutableList<EventDispatcher>) {
        dispatchers.add(CommonEventDispatcher())
    }

    override fun onResume() {
        try {
            super.onResume()
        } catch (e: RuntimeException) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            supportFragmentManager.dump("", null, pw, null)
            Log.wtf(TAG, "onResume e:$e, dump:$sw")
        }

        BaseEventBus.getInstance().register(mEventSubscriber)
    }

    override fun onPause() {
        super.onPause()
        try {
            BaseEventBus.getInstance().unregister(mEventSubscriber)
        } catch (ignored: IllegalStateException) {
        } catch (ignored: IllegalArgumentException) {
        }
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy - intent:$intent")
        for (dispatcher in eventDispatchers) {
            dispatcher.onActivityDestroy()
        }

        try {
            super.onDestroy()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "error at onDestroy: $this, e:$e")
        }
    }

    protected open fun handleBackPressed(): Boolean {
        return false
    }

    override fun onBackPressed() {
        onBackPressed(true)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return if (propagateOnKeyUp(keyCode, event)) {
            true
        } else {
            when (event.keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    onBackPressed(true)
                    true
                }
                else -> super.onKeyUp(keyCode, event)
            }
        }
    }

    fun onBackPressed(withChainingToChild: Boolean) {
        val handled = handleBackPressed()
        if (handled) {
            Log.d(TAG, "onBackPressed handled by Activity")
            return
        }

        val fragmentManager = supportFragmentManager
        val f = fragmentManager.primaryNavigationFragment
        if (withChainingToChild) {
            if (f is BaseFragment) {
                try {
                    if (f.onBackPressed()) {
                        Log.d(TAG, "onBackPressed handled by $f")
                        return
                    }
                } catch (e: IllegalStateException) {
                    Log.w(TAG, "onBackPress error: $e")
                }
            }
        }
        if (fragmentManager.backStackEntryCount > 1) {
            //showMainToolbar();
            try {
                fragmentManager.popBackStack()
            } catch (e: IllegalStateException) {
                Log.w(TAG, "onPopBackStack error: $e")
            }
        } else {
            val upIntent = supportParentActivityIntent
            if (upIntent != null) {
                finish()
                if (supportShouldUpRecreateTask(upIntent) || isTaskRoot) {
                    val b = TaskStackBuilder.create(this)
                    onCreateSupportNavigateUpTaskStack(b)
                    onPrepareSupportNavigateUpTaskStack(b)
                    b.startActivities()
                }
            } else {
                finish()
            }
        }
    }

    override fun onLowMemory() {
        if (Log.isLoggable(DebugHelper.TAG_MEMORY)) {
            Log.d(DebugHelper.TAG_MEMORY, "onLowMemory")
        }
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        if (Log.isLoggable(DebugHelper.TAG_MEMORY)) {
            Log.d(DebugHelper.TAG_MEMORY, "onTrimMemory - level:$level, activity:$this")
        }
        super.onTrimMemory(level)
        try {
            Glide.get(this).trimMemory(level)
        } catch (ignored: Exception) {
        }
    }

    private fun propagateOnKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        val fragment = supportFragmentManager.primaryNavigationFragment as BaseFragment?
        if (fragment != null) {
            return when (event?.keyCode ?: keyCode) {
                KeyEvent.KEYCODE_BACK -> false
                else -> fragment.onKeyUp(keyCode, event)
            }
        }
        return false
    }

    fun addFragment(type: BaseFragmentType, args: Bundle, extraArgs: Map<String, Any>?) {
        val frag = type.createFragment(args, extraArgs)
        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setCustomAnimations(type)
            .replace(R.id.content, frag)
            .setPrimaryNavigationFragment(frag)
            .addToBackStack(null)
            .commit()
    }

    fun replaceFragment(type: BaseFragmentType, args: Bundle?, clearAll: Boolean) {
        val frag = type.createFragment(args, null)
        if (clearAll) {
            while (supportFragmentManager.popBackStackImmediate()) {
            }
        } else {
            supportFragmentManager.popBackStackImmediate()
        }
        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setCustomAnimations(type)
            .replace(R.id.content, frag)
            .addToBackStack(null)
            .setPrimaryNavigationFragment(frag)
            .commit()
    }

    private fun isSameFragment(
        curType: BaseFragmentType,
        curArgs: Bundle?,
        type: BaseFragmentType,
        args: Bundle
    ): Boolean {
        if (curType != type) {
            return false
        }
        val argKeys = ArrayList<String>()
        for (key in argKeys) {
            val currentArgValue = curArgs?.get(key)
            val newArgValue = args.get(key)
            if (currentArgValue == null && newArgValue == null) {
                continue
            }
            if (currentArgValue == null || currentArgValue != newArgValue) {
                return false
            }
        }
        return true
    }

    fun showFragment(event: ShowFragmentEvent) {
        val fragmentManager = supportFragmentManager ?: return

        val type = event.fragmentType
        val args = event.args
        val transitionType: ShowFragmentEvent.Type? = event.transitionType
        val extraArguments = event.extraArguments

        val curFrag = fragmentManager.primaryNavigationFragment
        val curType = BaseFragmentType.getType(curFrag)

        if (curFrag == null || curType == null || !isSameFragment(curType, curFrag.arguments, type, args)) {
            when (transitionType) {
                ShowFragmentEvent.Type.ADD -> addFragment(type, args, extraArguments)
                ShowFragmentEvent.Type.ADD_WITH_MATERIAL_TRANSITION -> {
                    val eligible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    if (eligible) {
                        val transitions = event.materialTransitions
                        if (transitions != null) {
                            addFragmentWithMaterialTransition(type, args, extraArguments, transitions)
                        } else {
                            addFragment(type, args, extraArguments)
                        }
                    } else {
                        addFragment(type, args, extraArguments)
                    }
                }
                ShowFragmentEvent.Type.CLEAR_ALL -> replaceFragment(type, args, true)
                ShowFragmentEvent.Type.REPLACE -> replaceFragment(type, args, false)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addFragmentWithMaterialTransition(
        type: BaseFragmentType, args: Bundle,
        extraArguments: Map<String, Any>?, transitions: MaterialTransitions
    ) {
        val frag = type.createFragment(args, extraArguments)
        frag.sharedElementEnterTransition = transitions.sharedElementEnterTransition
        frag.sharedElementReturnTransition = transitions.sharedElementReturnTransition
        frag.enterTransition = transitions.enterTransition
        frag.exitTransition = transitions.exitTransition
        frag.reenterTransition = transitions.reenterTransition
        frag.returnTransition = transitions.returnTransition
        supportFragmentManager.beginTransaction()
            .replace(R.id.content, frag)
            .addToBackStack(null)
            .setCustomAnimations(
                transitions.enterAnimation,
                transitions.exitAnimation,
                transitions.reenterAnimation,
                transitions.returnAnimation
            )
            .apply {
                if (transitions.postponeEnterTransition) {
                    setReorderingAllowed(true)
                    frag.postponeEnterTransition()
                }
                for ((view, name) in transitions.sharedElements) {
                    addSharedElement(view, name)
                }
            }
            .commitAllowingStateLoss()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isOpaqueActivity()) {
            window.setFormat(PixelFormat.RGB_565)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val actionBar = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home, android.R.id.home -> {
                val frag = supportFragmentManager.findFragmentById(R.id.content) as BaseFragment?
                frag?.onOptionsItemSelected(item)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BaseConstant.Permission.REQUEST_CODE_FROM_EVENT) {
            BaseEventBus.getInstance().post(
                PermissionResponseEvent(
                    permissions,
                    grantResults
                )
            )
        }
    }

    private fun FragmentTransaction.setCustomAnimations(type: BaseFragmentType): FragmentTransaction {
        val animEnter: Int
        val animExit: Int
        val animPopEnter: Int
        val animPopExit: Int
        when (type) {
            BaseFragmentType.HOME -> {
                // no fragment animations
                animEnter = R.anim.no_anim
                animExit = R.anim.no_anim
                animPopEnter = R.anim.no_anim
                animPopExit = R.anim.no_anim
            }
            else -> {
                animEnter = R.anim.frag_enter
                animExit = R.anim.frag_exit
                animPopEnter = R.anim.frag_pop_enter
                animPopExit = R.anim.frag_pop_exit
            }
        }
        return setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit)
    }
}