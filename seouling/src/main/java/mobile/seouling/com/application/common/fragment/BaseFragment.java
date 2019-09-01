package mobile.seouling.com.application.common.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import mobile.seouling.com.R;
import mobile.seouling.com.application.events.ActivityBackPressEvent;
import mobile.seouling.com.application.events.FragmentBackPressEvent;
import mobile.seouling.com.framework.BaseEventBus;
import mobile.seouling.com.framework.Log;
import mobile.seouling.com.framework.util.KeyboardUtils;
import mobile.seouling.com.widget.BaseToolbar;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.*;

public abstract class BaseFragment extends RxFragment {

    public static final int VISIBILITY_CHANGE_REASON_UNKNOWN = 0;
    public static final int VISIBILITY_CHANGE_REASON_ON_START = 1;
    public static final int VISIBILITY_CHANGE_REASON_ON_STOP = 2;

    public static class VisibilityChangeEvent {
        public VisibilityChangeEvent(boolean visible, int reason) {
            this.visible = visible;
            this.reason = reason;
        }

        public final boolean visible;
        public final int reason;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            VisibilityChangeEvent that = (VisibilityChangeEvent) o;

            return visible == that.visible;

        }

        @Override
        public int hashCode() {
            return (visible ? 1 : 0);
        }

        @Override
        public String toString() {
            return "VisibilityChangeEvent{" +
                    "visible=" + visible +
                    ", reason=" + reason +
                    '}';
        }
    }

    private BehaviorSubject<VisibilityChangeEvent> mVisibilityChangeObservable;

    public Observable<VisibilityChangeEvent> getVisibilityChangeObservable() {
        if (mVisibilityChangeObservable == null) {
            mVisibilityChangeObservable = BehaviorSubject.createDefault(new VisibilityChangeEvent(isVisibleToUser(), 0));
        }
        return mVisibilityChangeObservable;
    }

    private static class FragmentMetaData {
        public SparseArray<Field> findViewTargets;
    }

    protected static final Handler sHandler = new Handler(Looper.getMainLooper());
    private static HashMap<Class, FragmentMetaData> sFragmentMetaData;

    private boolean mIsActivityCreated = false;
    private boolean mViewDestroyed = false;

    private boolean mIsVisibleToUser = false;
    private Map<String, Object> mExtraArguments;

    private BaseToolbar mCustomToolbar;

    //private VingleUrl mRecentPageUrl;

    /**
     * This makes log hard to read after proguard is applied.
     */
    private final String TAG = ((Object) this).getClass().getSimpleName();

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFragmentMetaData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setSoftInputMode();
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        findViews(view);
        super.onViewCreated(view, savedInstanceState);
        setCustomToolbar(view);
        // view is re-attaching
        if (mViewDestroyed) {
            mViewDestroyed = false;
        }
    }

    private void setCustomToolbar(View view) {
        View toolbarView = view.findViewById(R.id.toolbar);
        if (toolbarView instanceof BaseToolbar) {
            mCustomToolbar = (BaseToolbar) toolbarView;
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null && shouldUseSupportActionBar()) {
                activity.setSupportActionBar(mCustomToolbar);
            }
            onCustomToolbarInit(mCustomToolbar);
        }
    }

    protected void onCustomToolbarInit(@NonNull Toolbar toolbar) {
        // init custom toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onUpIconClick()) {
                    FragmentActivity activity = getActivity();
                    if (activity != null && activity.getCurrentFocus() instanceof EditText) {
                        KeyboardUtils.hide(activity);
                    }
                    BaseEventBus.getInstance().post(new ActivityBackPressEvent());
                }
            }
        });
    }

    private void findViews(View v) {
        ArrayList<SparseArray<Field>> maps = new ArrayList<>();
        Class clazz = getClass();
        while (clazz != BaseFragment.class) {
            FragmentMetaData meta = getFragmentMetaData(clazz);
            if (meta != null && meta.findViewTargets != null) {
                maps.add(meta.findViewTargets);
            }
            clazz = clazz.getSuperclass();
        }
        findViews(v, maps);
    }

    private void findViews(View v, ArrayList<SparseArray<Field>> maps) {
        int id = v.getId();
        if (id != View.NO_ID) {
            int size = maps.size();
            for (int i = 0; i < size; i++) {
                Field field = maps.get(i).get(id);
                if (field != null) {
                    try {
                        field.set(this, v);
                    } catch (Exception ignored) {
                    }
                    break;
                }
            }
        }
        if (v instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) v;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; ++i) {
                findViews(viewGroup.getChildAt(i), maps);
            }
        }
    }

    private void setSoftInputMode() {
        if (!isAdded()) {
            return;
        }
        int softInputMode = getDefaultSoftInputMode();
        if (softInputMode != WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED) {
            getActivity().getWindow().setSoftInputMode(softInputMode);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIsActivityCreated = true;
        //setHasOptionsMenu() should be called here!
        //Some LG phones call onCreateOptionsMenu() immediately after calling setHasOptionsMenu()
        //So setHasOptionsMenu() should not be called after onCreateView().
        setHasOptionsMenu(shouldHaveOptionsMenu());
    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        if (!enter) {
            return super.onCreateAnimation(transit, false, nextAnim);
        }
        if (nextAnim == 0) {
            return null;
        }
        try {
            return AnimationUtils.loadAnimation(requireContext(), nextAnim);
        } catch (Resources.NotFoundException exception) {
            return super.onCreateAnimation(transit, true, nextAnim);
        }
    }

    /**
     * @return Return true if you want to show options menu; default value is true.
     */
    @Deprecated
    protected boolean shouldHaveOptionsMenu() {
        return isRootFragment() && shouldUseSupportActionBar();
    }

    public int getContainerViewId() {
        return R.id.content;
    }

    private void dispatchVisibilityChange(boolean visible, int reason) {

        boolean wasVisible = mIsVisibleToUser;

        mIsVisibleToUser = visible;

        // Revises the visibility if this is a child Fragment but not current
        if (getParentFragment() instanceof BaseFragmentContainer) {
            BaseFragmentContainer cf = (BaseFragmentContainer) getParentFragment();
            if (cf instanceof BaseFragment && visible) {
                mIsVisibleToUser = ((BaseFragment) cf).isVisibleToUser();
            }
            if (!this.equals(cf.getCurrentChildFragment())) {
                mIsVisibleToUser = false;
            }
        }

        if (mIsVisibleToUser != wasVisible) {
            onFragmentVisibilityChanged(mIsVisibleToUser, reason);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        dispatchVisibilityChange(!isHidden() && getUserVisibleHint(), VISIBILITY_CHANGE_REASON_ON_START);
    }

    @Override
    public void onStop() {
        super.onStop();
        dispatchVisibilityChange(false, VISIBILITY_CHANGE_REASON_ON_STOP);
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        mViewDestroyed = true;
        mCustomToolbar = null;
        unbindDrawables(getView());
        nullifyViews();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mVisibilityChangeObservable != null) {
            mVisibilityChangeObservable.onComplete();
            mVisibilityChangeObservable = null;
        }
        super.onDestroy();
    }

    private void nullifyViews() {
        Class clazz = getClass();
        while (clazz != BaseFragment.class) {
            FragmentMetaData meta = getFragmentMetaData(clazz);
            if (meta != null && meta.findViewTargets != null) {
                SparseArray<Field> findViewTargets = meta.findViewTargets;
                int size = findViewTargets.size();
                for (int i = 0; i < size; ++i) {
                    Field field = findViewTargets.valueAt(i);
                    try {
                        field.set(this, null);
                    } catch (IllegalAccessException ignored) {
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public boolean isViewCreated() {
        return getView() != null;
    }

    private static void unbindDrawables(@Nullable View targetView) {
        if (targetView == null) {
            return;
        }
        final WeakReference<View> targetViewRef = new WeakReference<>(targetView);
        targetView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Queue<View> views = new LinkedList<>();
                View view = targetViewRef.get();
                if (view != null) {
                    views.offer(view);
                }
                while (!views.isEmpty()) {
                    view = views.poll();
                    Drawable background = view.getBackground();
                    if (background != null) {
                        background.setCallback(null);
                        try {
                            view.setBackground(null);
                        } catch (Exception ignored) {
                            // FloatingActionButton.setBackground(null) throws an exception
                        }
                    }
                    if (view instanceof ImageView) {
                        ImageView imageView = (ImageView) view;
                        view.setTag(null);
                        Drawable d = imageView.getDrawable();
                        if (d != null) {
                            d.setCallback(null);
                        }
                        imageView.setImageDrawable(null);
                    } else if (view instanceof ViewGroup) {
                        ViewGroup parent = (ViewGroup) view;
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            views.offer(parent.getChildAt(i));
                        }
                    }
                }
            }
        }, 4000);
    }

    @Nullable
    public ActionBar getSupportActionBar() {
        FragmentActivity activity = getActivity();
        return null;
    }

    @Override
    public final void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isVisibleToUser()) {
            onCreateOptionsMenuImpl(menu, inflater);
        }
    }

    /**
     * @return Return true if you don't use default_menu; if you want to use default_menu then return false.
     */
    protected boolean onCreateOptionsMenuImpl(Menu menu, MenuInflater inflater) {
        return false;
    }

    public boolean onBackPressed() {
        return false;
    }

    @SuppressWarnings("UnusedParameters")
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * @return true if subclass handled the event, false otherwise
     */
    public boolean onUpIconClick() {
        return false;
    }

    /**
     * More strict Fragment visibility check
     *
     * @return true if this Fragment is visible to user currently, false otherwise
     */
    public boolean isVisibleToUser() {
        return mIsVisibleToUser;
    }

    protected void onFragmentVisibilityChanged(boolean visible, int reason) {
        //setLifecycleEvent(visible ? SHOW : HIDE);
        if (visible) {
            setSoftInputMode();
            if (isRootFragment()) {
                dispatchDisplayTitle();
                updateToolbarType();
            }
            if (mIsVisibleToUser) {
                //sendGaView();
            }
        }
        if (mVisibilityChangeObservable != null) {
            mVisibilityChangeObservable.onNext(new VisibilityChangeEvent(visible, reason));
        }
    }

    protected void updateToolbarType() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            if (mCustomToolbar != null) {
                if (shouldUseSupportActionBar()) {
                    activity.setSupportActionBar(mCustomToolbar);
                }
                onCustomToolbarInit(mCustomToolbar);
            }
        }
    }

    public boolean isRootFragment() {
        return getParentFragment() == null;
    }

    protected final boolean dispatchDisplayTitle() {
        //Toolbar toolbar = getToolbar();
        //return toolbar != null && onDisplayTitle(toolbar);
        return false;
    }

    @Nullable
    public BaseToolbar getToolbar() {
        return mCustomToolbar;
    }

    protected void setToolbarBackgroundAlpha(float alpha) {
        BaseToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setBackgroundAlpha(alpha);
        }
    }

    protected boolean onDisplayTitle(@NonNull Toolbar toolbar) {
        return false;
    }

    @Override
    public void setHasOptionsMenu(final boolean hasMenu) {
        super.setHasOptionsMenu(hasMenu);
    }

    @Override
    public void setMenuVisibility(final boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    protected int getDefaultSoftInputMode() {
        return WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        dispatchVisibilityChange(!hidden && getUserVisibleHint(), VISIBILITY_CHANGE_REASON_UNKNOWN);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        try {
            super.setUserVisibleHint(isVisibleToUser);
        } catch (NullPointerException npe) {
            Log.w(TAG, "setUserVisibleHint error - isVisibleToUser:" + isVisibleToUser);
        }
        if (getActivity() != null && getView() != null) {
            dispatchVisibilityChange(!isHidden() && isVisibleToUser, VISIBILITY_CHANGE_REASON_UNKNOWN);
        }
    }

    public View getRootContainerView() {
        BaseFragment f = this;
        Fragment parent = f.getParentFragment();
        while (parent != null) {
            f = (BaseFragment) parent;
            parent = f.getParentFragment();
        }
        return f.getView();
    }

    public Map<String, Object> getExtraArguments() {
        return mExtraArguments;
    }

    public void setExtraArguments(Map<String, Object> extraArguments) {
        mExtraArguments = extraArguments;
    }

    @Nullable
    private static FragmentMetaData getFragmentMetaData(Class clazz) {
        return sFragmentMetaData == null ? null : sFragmentMetaData.get(clazz);
    }

    private static void setFragmentMetaData(Class clazz, @NonNull FragmentMetaData meta) {
        if (sFragmentMetaData == null) {
            sFragmentMetaData = new HashMap<>();
        }
        sFragmentMetaData.put(clazz, meta);
    }

    private void loadFragmentMetaData() {
        Class clazz = getClass();
        while (clazz != BaseFragment.class) {
            FragmentMetaData meta = getFragmentMetaData(clazz);
            if (meta == null) {
                SparseArray<Field> findViewTargets = null;
//                for (Field field : clazz.getDeclaredFields()) {
//                    FindView findView = field.getAnnotation(FindView.class);
//                    if (findView != null) {
//                        if (findViewTargets == null) {
//                            findViewTargets = new SparseArray<>();
//                        }
//                        field.setAccessible(true);
//                        findViewTargets.put(findView.value(), field);
//                    }
//                }

                meta = new FragmentMetaData();
                meta.findViewTargets = findViewTargets;
                setFragmentMetaData(clazz, meta);
            }
            clazz = clazz.getSuperclass();
        }
    }

    public Completable destroySignal() {
        return getLifecycleSignal(FragmentEvent.DESTROY_VIEW);
    }

    protected boolean isFirstViewCreation() {
        return !mIsActivityCreated;
    }

    protected boolean shouldUseSupportActionBar() {
        return true;
    }

    public void performBackPress() {
        BaseEventBus.getInstance().post(new FragmentBackPressEvent());
    }
}