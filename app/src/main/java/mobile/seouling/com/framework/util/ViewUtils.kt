package mobile.seouling.com.framework.util

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import mobile.seouling.com.base_android.toPixels
import java.lang.ref.WeakReference
import java.util.concurrent.CancellationException

object ViewUtils {
    private const val TAG = "ViewUtils"

    @JvmField
    val STATE_SET_CHECKED = intArrayOf(android.R.attr.state_checked)

    fun updateShadowLayerOffsetByDP(view: TextView, radius: Int, dx: Int, dy: Int, shadowColor: Int) {
        val context = view.context
        val dxInPixel = context.toPixels(value = dx)
        val dyInPixel = context.toPixels(value = dy)
        updateShadowLayerOffsetByPixel(
            view,
            radius,
            dxInPixel,
            dyInPixel,
            shadowColor
        )
    }

    fun updateShadowLayerOffsetByPixel(view: TextView, radius: Int, dx: Int, dy: Int, @ColorRes shadowColor: Int) {
        view.setShadowLayer(
            radius.toFloat(),
            dx.toFloat(),
            dy.toFloat(),
            ContextCompat.getColor(view.context, shadowColor)
        )
    }

    @SuppressLint("NewApi")
    fun removeOnGlobalLayoutListener(view: View?, listener: ViewTreeObserver.OnGlobalLayoutListener) {
        if (view == null) return
        val viewTreeObserver = view.viewTreeObserver
        if (!viewTreeObserver.isAlive) return
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    @JvmStatic
    fun addFirstGlobalLayoutListener(view: View?, listener: OnGlobalLayoutListener?) {
        if (view == null || listener == null) return

        val viewRef = WeakReference(view)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val v = viewRef.get()
                if (v != null) {
                    removeOnGlobalLayoutListener(v, this)
                    try {
                        listener.onGlobalLayout(v)
                    } catch (e: Exception) {
                        // https://vingle.atlassian.net/browse/ANDR-3845
                        Log.wtf(TAG, "onGlobalLayout error: $e")
                    }
                }
            }
        })
    }

    fun addFirstGlobalLayoutListener(view: View?, listener: ((View?) -> Unit)?) {
        addFirstGlobalLayoutListener(
            view,
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout(view: View?) {
                    listener?.invoke(view)
                }
            })
    }

    interface OnGlobalLayoutListener {
        fun onGlobalLayout(view: View?)
    }

    fun setIndeterminateTintList(v: ProgressBar, tint: ColorStateList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            v.indeterminateTintList = tint
        } else {
            // ?
        }
    }

    @JvmStatic
    fun ensureLayout(v: View?): Completable {
        return if (v == null || ViewCompat.isLaidOut(v)) {
            Completable.complete()
        } else {
            Completable.create { subscriber ->
                v.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                    override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int,
                                                oldTop: Int, oldRight: Int, oldBottom: Int) {
                        v.removeOnLayoutChangeListener(this)
                        subscriber.onComplete()
                    }
                })
            }
        }
    }

    fun inflateAsync(@LayoutRes resId: Int, parent: ViewGroup, attachToRoot: Boolean): Single<View> {
        //        cause memory leak
        //        AsyncLayoutInflater inflater = new AsyncLayoutInflater(parent.getContext());
        //        return Single.create(subscriber -> {
        //            inflater.inflate(resId, parent, (view, r, p) -> {
        //                if (attachToRoot) {
        //                    p.addView(view);
        //                }
        //                subscriber.onSuccess(view);
        //            });
        //        });
        //
        val parentRef = WeakReference(parent)
        return Single.defer {
            parentRef.get()?.let {
                val view = LayoutInflater.from(it.context).inflate(resId, it, false)
                if (attachToRoot) {
                    it.addView(view)
                }
                Single.just(view)
            } ?: Single.error<View>(CancellationException())
        }
    }

    fun textureViewCenterCrop(view: TextureView, videoWidth: Int, videoHeight: Int) {
        val viewWidth = view.width.toFloat()
        val viewHeight = view.height.toFloat()
        val viewRatio = viewWidth / viewHeight
        val videoRatio = videoWidth.toFloat() / videoHeight

        var scaleX = 1f
        var scaleY = 1f

        if (videoRatio < viewRatio) {
            scaleY = viewRatio / videoRatio
        } else {
            scaleX = videoRatio / viewRatio
        }

        // Calculate pivot points, in our case crop from center
        val pivotPointX = Math.round(viewWidth / 2)
        val pivotPointY = Math.round(viewHeight / 2)

        val matrix = Matrix()
        matrix.setScale(scaleX, scaleY, pivotPointX.toFloat(), pivotPointY.toFloat())
        view.setTransform(matrix)
    }

    fun ensureWidth(view: View, width: Int): Single<Int> {
        return if (width == 0) {
            ensureLayout(view)
                .andThen(Single.just(view.width))
                .onErrorReturn { 0 }
        } else {
            Single.just(width)
        }
    }

    @JvmStatic
    fun setHeight(v: View, height: Int) {
        val lp = v.layoutParams
        if (lp != null && lp.height != height) {
            lp.height = height
            v.requestLayout()
        }
    }

    fun setWidth(v: View, width: Int) {
        val lp = v.layoutParams
        if (lp == null || lp.width == width) {
            return
        }
        lp.width = width
        v.requestLayout()
    }

    @JvmStatic
    fun makeTextViewResizable(
        tv: TextView,
        width: Int,
        widthFeedback: Consumer<Int>?,
        maxLine: Int,
        originalText: String,
        expandText: String,
        expandTextColor: Int
    ): Completable {
        val expandTextWithDots = "... $expandText"
        tv.text = String.format("%s%s", originalText, "")
        tv.maxLines = maxLine
        val completable: Completable = if (width == 0) {
            ensureLayout(tv)
        } else {
            tv.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            Completable.complete()
        }
        return completable.concatWith { emitter ->
            if (widthFeedback != null) {
                try {
                    widthFeedback.accept(tv.width)
                } catch (e: Exception) {
                    emitter.onError(e)
                }
            }
            if (maxLine > 0 && tv.lineCount > maxLine) {
                val lastLineCutIndex = getLastLineCutIndex(
                    tv,
                    maxLine - 1,
                    expandTextWithDots
                )
                val text = tv.text.subSequence(0, lastLineCutIndex).toString() + expandTextWithDots
                tv.text = text
                val clickableStringBuilder =
                    addClickablePartTextViewResizable(
                        SpannableString(tv.text.toString()), tv, originalText, expandText,
                        expandTextColor
                    )
                tv.setText(clickableStringBuilder, TextView.BufferType.SPANNABLE)
            }
            emitter.onComplete()
        }
    }

    private fun getLastLineCutIndex(tv: TextView, lastLineIndex: Int, expandText: String): Int {
        val textViewLayout = tv.layout
        val textPaint = tv.paint
        val lastLineStart = textViewLayout.getLineStart(lastLineIndex)
        var lastLineEnd = textViewLayout.getLineEnd(lastLineIndex)
        var secondLineString = textViewLayout.text.subSequence(lastLineStart, lastLineEnd).toString()

        val expandTextWidth = textPaint.measureText(expandText)
        val textViewWidth = tv.measuredWidth

        while (textViewWidth < textPaint.measureText(secondLineString) + expandTextWidth
            || secondLineString.endsWith("\n")) {
            secondLineString = textViewLayout.text.subSequence(lastLineStart, --lastLineEnd).toString()
        }
        return lastLineEnd - 1
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned,
        tv: TextView,
        originalText: String,
        spanableText: String,
        spanableTextColor: Int
    ): SpannableStringBuilder {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        val spanStartIndex = str.lastIndexOf(spanableText)
        if (spanStartIndex != -1) {
            val spanEndIndex = spanStartIndex + spanableText.length
            ssb.setSpan(ForegroundColorSpan(spanableTextColor), spanStartIndex, spanEndIndex, 0)
        }
        return ssb
    }

    @JvmStatic
    fun removeSelf(v: View?) {
        if (v == null) return
        val id = v.id
        if (id != 0) {
            var view = v
            while (view != null) {
                view.setTag(id, null)
                val parent = view.parent
                view = if (parent is View) parent else null
            }
        }
        val parent = v.parent
        if (parent is ViewGroup) {
            parent.removeView(v)
        }
    }

    @JvmStatic
    fun getRelativeBounds(container: View, target: View): Rect? {
        val bounds = Rect()
        return if (getRelativeBounds(container, target, bounds)) bounds else null
    }

    fun getRelativeBounds(container: View?, target: View?, out: Rect): Boolean {
        var view = target
        if (container == null || view == null) return false
        if (container === view) {
            out.set(0, 0, view.width, view.height)
            return true
        }
        val w = view.width
        val h = view.height
        out.top = 0
        out.left = out.top
        while (true) {
            val parent = view!!.parent
            if (parent == null || parent !is ViewGroup) {
                return false
            }
            out.left += view.left - parent.scrollX
            out.top += view.top - parent.scrollY
            if (parent === container) {
                out.right = out.left + w
                out.bottom = out.top + h
                return true
            }
            view = parent
        }
    }

    @JvmStatic
    fun getWrappedRelativeBounds(container: View?, target: View?): Rect? {
        val rect = Rect()
        return if (getWrappedRelativeBounds(container, target, rect)) rect else null
    }

    fun getWrappedRelativeBounds(container: View?, target: View?, out: Rect): Boolean {
        if (container == null || target == null) return false
        if (!container.getGlobalVisibleRect(out)) return false
        val containerX = out.left
        val containerY = out.top
        if (!target.getGlobalVisibleRect(out)) return false
        out.offset(-containerX, -containerY)
        return true
    }

    @JvmStatic
    @JvmOverloads
    fun findNearestParent(v: View?, id: Int, includeSelf: Boolean = false): ViewGroup? {
        if (v == null) return null
        if (includeSelf && v is ViewGroup && v.id == id) return v
        var parent = v.parent
        while (parent is ViewGroup) {
            val viewGroup = parent
            if (viewGroup.id == id) {
                return viewGroup
            }
            parent = viewGroup.parent
        }
        return null
    }

    fun setPadding(view: View, padding: Rect) {
        view.setPadding(padding.left, padding.top, padding.right, padding.bottom)
    }

    fun setChildrenVisibility(viewGroup: ViewGroup, visibility: Int) {
        val childCount = viewGroup.childCount
        for (i in 0 until childCount) {
            val child = viewGroup.getChildAt(i)
            child.visibility = visibility
        }
    }

    fun setCursorDrawableColor(editText: EditText, color: Int) {
        // http://stackoverflow.com/a/26543290/3162076
        try {
            val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            fCursorDrawableRes.isAccessible = true
            val mCursorDrawableRes = fCursorDrawableRes.getInt(editText)
            val fEditor = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor.get(editText)
            val clazz = editor.javaClass
            val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
            fCursorDrawable.isAccessible = true
            val drawables = arrayOfNulls<Drawable>(2)
            val res = editText.resources
            drawables[0] = ResourcesCompat.getDrawable(res, mCursorDrawableRes, null)?.apply {
                setColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
            drawables[1] = ResourcesCompat.getDrawable(res, mCursorDrawableRes, null)?.apply {
                setColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
            fCursorDrawable.set(editor, drawables)
        } catch (ignored: Throwable) {
        }
    }

    fun getParent(view: View): ViewGroup? = view.parent as ViewGroup

    fun removeView(view: View) {
        val parent = getParent(view)
        parent?.removeView(view)
    }

    fun replaceView(currentView: View, newView: View) {
        val parent = getParent(currentView) ?: return
        val index = parent.indexOfChild(currentView)
        removeView(currentView)
        removeView(newView)
        parent.addView(newView, index)
    }

    fun hitTest(v: View, x: Int, y: Int): Boolean {
        val tx = (v.translationX + 0.5f).toInt()
        val ty = (v.translationY + 0.5f).toInt()
        val left = v.left + tx
        val right = v.right + tx
        val top = v.top + ty
        val bottom = v.bottom + ty
        return x in left..right && y >= top && y <= bottom
    }
}