package mobile.seouling.com.application.events;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import mobile.seouling.com.application.common.BaseActivity;
import mobile.seouling.com.application.common.fragment.BaseFragmentType;
import mobile.seouling.com.framework.Log;
import mobile.seouling.com.framework.activity_helper.EventDispatcher;
import mobile.seouling.com.framework.activity_helper.MaterialTransitions;
import mobile.seouling.com.framework.util.KeyboardUtils;

import java.util.Map;

public class ShowFragmentEvent implements IGetArgs {

    protected static final String TAG = "ShowFragmentEvent";

    private static final String KEY_SCREEN_FROM = "screen_from";

    @NonNull
    private final BaseFragmentType fragmentType;

    public enum Type {
        ADD,
        ADD_WITH_MATERIAL_TRANSITION,
        CLEAR_ALL,
        REPLACE
    }

    private final String screenFrom;

    @NonNull
    private final Type transitionType;

    public static ShowFragmentEvent from(@NonNull BaseFragmentType fType) {
        return new ShowFragmentEvent(fType);
    }

    protected ShowFragmentEvent(@NonNull BaseFragmentType fragmentType) {
        this(Type.ADD, fragmentType, null);
    }

    protected ShowFragmentEvent(@NonNull BaseFragmentType fragmentType, String screenFrom) {
        this(Type.ADD, fragmentType, screenFrom);
    }

    protected ShowFragmentEvent(@NonNull Type type, @NonNull BaseFragmentType fragmentType) {
        this(type, fragmentType, null);
    }

    protected ShowFragmentEvent(@NonNull Type type, @NonNull BaseFragmentType fragmentType, String screenFrom) {
        this.transitionType = type;
        this.fragmentType = fragmentType;
        this.screenFrom = screenFrom;
    }

    @NonNull
    public Type getTransitionType() {
        return transitionType;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    public MaterialTransitions getMaterialTransitions() {
        return null;
    }

    @Nullable
    public Map<String, Object> getExtraArguments() {
        return null;
    }

    @NonNull
    public BaseFragmentType getFragmentType() {
        return fragmentType;
    }

    @Override
    public Bundle getArgs() {
        Bundle args = new Bundle();
        if (screenFrom != null) {
            args.putString(KEY_SCREEN_FROM, screenFrom);
        }
        return args;
    }

    @Override
    public String toString() {
        return "ShowFragmentEvent{" +
                "fragmentType=" + fragmentType +
                ", transitionType=" + transitionType +
                '}';
    }

    /**
     * Called before processing the given fragment-show request
     *
     * @return true will stop further executing, false otherwise
     */
    protected boolean onPreExecute(EventDispatcher dispatcher) {
        return false;
    }

    /**
     * Callback to be called after successful Fragment switching
     */
    protected void onPostExecute(EventDispatcher dispatcher) {
    }

    public final void executeEvent(EventDispatcher dispatcher) {
        boolean handled = onPreExecute(dispatcher);
        if (handled) {
            // execution cancelling
            return;
        }
        if (getFragmentType() != null) {
            try {
                final BaseActivity activity = dispatcher.getActivity();
                KeyboardUtils.hide(activity);
                activity.showFragment(this);
                onPostExecute(dispatcher);
            } catch (EventDispatcher.NullActivityException e) {
                Log.w(TAG, "executeEvent NullActivityException - event:" + this, e);
            }
        } else {
            Log.wtf(TAG, "event with null type:" + this);
        }
    }
}

