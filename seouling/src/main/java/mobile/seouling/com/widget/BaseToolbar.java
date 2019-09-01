package mobile.seouling.com.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import mobile.seouling.com.R;
import mobile.seouling.com.framework.util.ColorUtils;
import mobile.seouling.com.framework.util.ViewUtils;

public class BaseToolbar extends Toolbar {
    public static final String TAG = "VingleToolbar";

    public static int BORDER_BOTTOM_COLOR = 0xfff7f7f7;

    protected int mBorderTopColor;

    protected float mTitleAlpha = 1f;
    protected int mTitleColorWithoutAlpha;

    protected float mBackgroundAlpha = 1f;
    protected boolean mIsBackgroundMutated;

    protected int mTopBorderWidth;
    protected int mBottomBorderWidth;
    protected boolean mShowBottomBorder;
    protected boolean mShowTopBorder;
    protected Paint mBorderPaint;

    protected Drawable mNavigationIconDrawable;
    protected boolean mNavigationIconMutated;
    protected View mCustomizedTitleView;

    public BaseToolbar(Context context) {
        super(context);
        init(context, null, 0);
    }

    public BaseToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public BaseToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Toolbar, defStyleAttr, 0);
        int textAppearance = a.getResourceId(R.styleable.Toolbar_titleTextAppearance,
                R.style.TextAppearance_Widget_Base_Toolbar_Title);
        if (textAppearance != 0) {
            extractTitleColorFromTextAppearance(context, textAppearance);
            updateTitleTextColor();
        }
        a.recycle();

        a = context.obtainStyledAttributes(attrs, R.styleable.BaseToolbar);
        mShowTopBorder = a.getBoolean(R.styleable.BaseToolbar_showTopBorder, false);
        mShowBottomBorder = a.getBoolean(R.styleable.BaseToolbar_showBottomBorder, false);
        a.recycle();

        mBorderPaint = new Paint();

        initBorder(context);
    }

    protected void initBorder(Context context) {
        Resources res = getResources();
        mBottomBorderWidth = res.getDimensionPixelSize(R.dimen.toolbar_border_bottom_width);
        mBorderTopColor = ContextCompat.getColor(context, R.color.colorPrimary);
        mTopBorderWidth = res.getDimensionPixelSize(R.dimen.toolbar_border_top_width);
    }

    @Override
    public void setTitleTextColor(int color) {
        super.setTitleTextColor(color);
        mTitleColorWithoutAlpha = color & 0x00FFFFFF;
        updateTitleTextColor();
    }

    @Override
    public void setTitleTextAppearance(Context context, int resId) {
        super.setTitleTextAppearance(context, resId);
        extractTitleColorFromTextAppearance(context, resId);
        updateTitleTextColor();
    }

    protected void extractTitleColorFromTextAppearance(Context context, int resId) {
        TypedArray a = context.obtainStyledAttributes(resId, R.styleable.TextAppearance);
        int color = a.getColor(R.styleable.TextAppearance_android_textColor, 0xffffffff);
        mTitleColorWithoutAlpha = color & 0x00ffffff;
        a.recycle();
    }

    public void setTitleAlpha(float alpha) {
        if (mTitleAlpha != alpha) {
            mTitleAlpha = alpha;
            updateTitleTextColor();
        }
    }

    protected void updateTitleTextColor() {
        super.setTitleTextColor((int) (0xff * mTitleAlpha) << 24 | mTitleColorWithoutAlpha);
    }

    @Override
    public void setTitle(int resId) {
        super.setTitle(resId);
        removeCustomizedTitleView();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        removeCustomizedTitleView();
    }

    protected void removeCustomizedTitleView() {
        if (mCustomizedTitleView != null) {
            ViewUtils.removeSelf(mCustomizedTitleView);
            mCustomizedTitleView = null;
        }
    }

    public View setTitleView(int layoutId) {
        setTitle("");
        mCustomizedTitleView = LayoutInflater.from(getContext()).inflate(layoutId, this, false);
        super.addView(mCustomizedTitleView);
        return mCustomizedTitleView;
    }

    public void setTitleView(View v) {
        setTitle("");
        mCustomizedTitleView = v;
        super.addView(v);
    }

    public View getTitleView() {
        return mCustomizedTitleView;
    }

    void setPaddingRight(int padding) {
        if (getPaddingRight() != padding) {
            setPadding(getPaddingLeft(), getPaddingTop(), padding, getPaddingBottom());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // To avoid gpu overdrawing
        int saveCount = canvas.save();
        int height = getHeight();
        int bottom = height;
        if (mShowBottomBorder) {
            bottom -= mBottomBorderWidth;
        }
        int width = getWidth();
        canvas.clipRect(0, mTopBorderWidth, width, bottom);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(saveCount);

        int alphaInt = (int) (0xff * mBackgroundAlpha);
        if (mShowTopBorder) {
            mBorderPaint.setColor(ColorUtils.argb(alphaInt, mBorderTopColor));
            canvas.drawRect(0, 0, width, mTopBorderWidth, mBorderPaint);
        }
        if (mShowBottomBorder) {
            mBorderPaint.setColor(ColorUtils.argb(alphaInt, BORDER_BOTTOM_COLOR));
            canvas.drawRect(0, height - mBottomBorderWidth, width, height, mBorderPaint);
        }
    }

    public void setBackgroundAlpha(float alpha) {
        if (mBackgroundAlpha != alpha) {
            mBackgroundAlpha = alpha;
            Drawable background = getBackground();
            if (background != null) {
                if (!mIsBackgroundMutated && alpha != 1f) {
                    // Background should be mutated or it will affect other toolbar's background in other fragment.
                    background = background.mutate();
                    setBackground(background);
                    mIsBackgroundMutated = true;
                }
                background.setAlpha((int) (0xff * alpha));
            }
            invalidate();
        }
    }

    public void setShowBottomBorder(boolean show) {
        if (mShowBottomBorder != show) {
            mShowBottomBorder = show;
            int height = getHeight();
            invalidate(0, height - mBottomBorderWidth, getWidth(), height);
        }
    }

    public void setShowTopBorder(boolean show) {
        if (mShowTopBorder != show) {
            mShowTopBorder = show;
            invalidate(0, 0, getWidth(), mTopBorderWidth);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        mIsBackgroundMutated = false;
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        mIsBackgroundMutated = false;
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        mIsBackgroundMutated = false;
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        mIsBackgroundMutated = false;
    }

    @Override
    public void setNavigationIcon(Drawable icon) {
        super.setNavigationIcon(icon);
        mNavigationIconDrawable = icon;
        mNavigationIconMutated = false;
    }

    public void setNavigationIconTint(int color) {
        mutateNavigationIcon();
        Drawable wrappedDrawable = DrawableCompat.wrap(mNavigationIconDrawable);
        if (wrappedDrawable != mNavigationIconDrawable) {
            mNavigationIconDrawable = wrappedDrawable;
            super.setNavigationIcon(wrappedDrawable);
        }
        DrawableCompat.setTint(mNavigationIconDrawable, color);
        mNavigationIconDrawable.invalidateSelf();
    }

    protected void mutateNavigationIcon() {
        if (!mNavigationIconMutated) {
            mNavigationIconDrawable = mNavigationIconDrawable.mutate();
            super.setNavigationIcon(mNavigationIconDrawable);
            mNavigationIconMutated = true;
        }
    }
}
