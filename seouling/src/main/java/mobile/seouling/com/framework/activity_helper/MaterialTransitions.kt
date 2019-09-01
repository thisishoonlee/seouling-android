package mobile.seouling.com.framework.activity_helper

import android.annotation.TargetApi
import android.os.Build
import android.transition.*
import android.view.View
import mobile.seouling.com.R
import kotlin.jvm.JvmOverloads

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class MaterialTransitions @JvmOverloads constructor(
        val sharedElements: Map<View, String> = emptyMap(),
        val sharedElementEnterTransition: Transition? = TransitionSet().apply {
        addTransition(ChangeTransform().apply { reparentWithOverlay = true })
        addTransition(ChangeBounds())
        },
        val sharedElementReturnTransition: Transition? = sharedElementEnterTransition,
        val enterTransition: Transition? = Fade(),
        val exitTransition: Transition? = null,
        val reenterTransition: Transition? = exitTransition,
        val returnTransition: Transition? = enterTransition,
        val enterAnimation: Int = 0,
        val exitAnimation: Int = R.anim.fade_out_short,
        val reenterAnimation: Int = 0,
        val returnAnimation: Int = R.anim.fade_out_short,
        val postponeEnterTransition: Boolean = false
        )

