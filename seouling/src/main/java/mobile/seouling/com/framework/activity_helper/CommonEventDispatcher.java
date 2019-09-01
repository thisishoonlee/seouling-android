package mobile.seouling.com.framework.activity_helper;


import android.content.ActivityNotFoundException;
import android.os.Build;
import android.view.KeyEvent;
import androidx.annotation.RequiresApi;
import com.squareup.otto.Subscribe;
import mobile.seouling.com.application.events.*;
import mobile.seouling.com.application.common.BaseActivity;
import mobile.seouling.com.framework.Log;

public class CommonEventDispatcher extends EventDispatcher {

    public static final String TAG = CommonEventDispatcher.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Subscribe
    public void onStartActivity(StartActivityEvent event) {
        try {
            BaseActivity activity = getActivity();
            if (activity.isForegroundState()) {
                activity.startActivity(event.intent);
            }
        } catch (ActivityNotFoundException | NullActivityException e) {
            if (event.errorHandler != null) {
                event.errorHandler.accept(e);
            }
        }
    }

    @Subscribe
    public void onShowFragment(ShowFragmentEvent event) {
        try {
            BaseActivity activity = getActivity();
            if (activity.isForegroundState()) {
                try {
                    event.executeEvent(this);
                } catch (Exception e) {
                    Log.w(TAG, "showFragment failed - event:" + event, e);
                }
            }
        } catch (ActivityNotFoundException | NullActivityException e) {
            Log.e(TAG, "onShowFragment failed - event:" + event, e);
            event.executeEvent(this);
        } catch (Exception e) {
            Log.w(TAG, "showFragment failed - event:" + event, e);
        }
    }
//
//    @Subscribe
//    public void onSendFeedback(SendFeedbackEvent event) {
//        try {
//            MallangBaseActivity act = getActivity();
//            if (act.isForegroundState()) {
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("message/rfc822");
//                intent.putExtra(Intent.EXTRA_EMAIL,
//                        new String[]{
//                                "support.android@vingle.net"
//                        }
//                );
//                AuthModel auth = AuthModel.get();
//                String username = auth == null ? "Guest" : auth.getUser().getUsername();
//                String model = DeviceHelper.getDeviceName();
//                String message = "------------------------\n"
//                        + "Vingle ID : " + username + "\n"
//                        + "Model : " + model + "\n"
//                        + "OS Version : " + Build.VERSION.RELEASE + "\n"
//                        + "App Version : " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")" + "\n"
//                        + "------------------------\n\n";
//
//                intent.putExtra(Intent.EXTRA_SUBJECT, "Vingle Android App Feedback");
//                intent.putExtra(Intent.EXTRA_TEXT, message);
//                act.startActivity(Intent.createChooser(intent, "Send Feedback"));
//            }
//        } catch (NullActivityException e) {
//            Log.e(TAG, "onSendFeedback - null activity", e);
//        }
//    }
//
//    @Subscribe
//    public void onShowLoading(ShowLoadingEvent event) {
//        try {
//            VingleBaseActivity act = getActivity();
//            if (act.isForegroundState()) {
//                FragmentManager fm = act.getSupportFragmentManager();
//                FragmentTransaction tr = fm.beginTransaction();
//                Fragment frag = fm.findFragmentByTag(LoadingFragment.class.getSimpleName());
//                if (frag == null) {
//                    frag = LoadingFragment.newInstance(event.message);
//                    tr.add(frag, LoadingFragment.class.getSimpleName());
//                    tr.show(frag);
//                }
//                tr.commitAllowingStateLoss();
//            }
//        } catch (NullActivityException ignored) {
//        }
//    }
//
//    @Subscribe
//    public void onHideLoading(final HideLoadingEvent event) {
//        try {
//            VingleBaseActivity act = getActivity();
//            if (act.isForegroundState()) {
//                FragmentManager fm = act.getSupportFragmentManager();
//                Fragment frag = fm.findFragmentByTag(LoadingFragment.class.getSimpleName());
//                if (frag != null && frag instanceof DialogFragment) {
//                    ((DialogFragment) frag).dismissAllowingStateLoss();
//                } else if (event.canRetry()) {
//                    act.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            event.decreaseRetryCount();
//                            VingleEventBus.getInstance().postDelayed(event, 500);
//                        }
//                    });
//                }
//            }
//        } catch (NullActivityException ignore) {
//        }
//    }
//
//    @Subscribe
//    public void onCancelLoading(LoadingCanceledEvent event) {
//        try {
//            VingleBaseActivity act = getActivity();
//            if (act.isForegroundState()) {
//                FragmentManager fm = act.getSupportFragmentManager();
//                Fragment frag = fm.findFragmentByTag(LoadingFragment.class.getSimpleName());
//                if (frag != null && frag instanceof DialogFragment) {
//                    ((DialogFragment) frag).dismissAllowingStateLoss();
//                }
//            }
//        } catch (NullActivityException ignore) {
//            //Log.e(TAG, "onCancelLoading - null activity", e);
//        }
//    }
//
//    @Subscribe
//    public void onSignout(SignoutEvent event) {
//        try {
//            VingleBaseActivity act = getActivity();
//            if (act instanceof VingleMainActivity) {
//                GCMHelper.unregisterGCM();
//                VingleFacebookHelper.clearToken();
//                VinglePreference.clearAuth();
//                VingleInstanceData.clearInstanceData();
//                VingleInitializer.releaseVingle();
//                VingleSNSData.setAuthentications(null);
//                AuthModel.set(null);
//                Model.release();
//                EditorDataSaver.clear(act);
//                VingleItActivity.setEnabled(act, false);
//                //OnboardingManager.with(act).clearAll();
//                /** on your logout method: **/
//                Intent broadcastIntent = new Intent();
//                broadcastIntent.setPackage(act.getPackageName());
//                broadcastIntent.setAction(VingleIntent.ACTION_SIGN_OUT);
//                broadcastIntent.putExtra("is_admin", event.isAdmin);
//                LocalBroadcastManager.getInstance(act).sendBroadcast(broadcastIntent);
//            }
//        } catch (NullActivityException ignore) {
//            Log.e(TAG, "onSignout - null activity", ignore);
//        }
//    }

    @Subscribe
    public void onFragmentBackPressed(FragmentBackPressEvent event) {
        try {
            BaseActivity act = getActivity();
            if (act.isForegroundState()) {
                act.onBackPressed(true);
            }
        } catch (NullActivityException ignore) {
            //Log.e(TAG, "onFragmentBackPressed - null activity", ignore);
        } catch (IllegalStateException stateException) {
            Log.w(TAG, "onFragmentBackPressed - illegalState: " + stateException);
        }
    }

    @Subscribe
    public void onActivityBackPressed(ActivityBackPressEvent event) {
        try {
            BaseActivity act = getActivity();
            if (act.isForegroundState()) {
                act.onBackPressed(false);
            }
        } catch (NullActivityException ignore) {
            Log.i(TAG, "onActivityBackPressed - null activity");
        } catch (IllegalStateException stateException) {
            Log.w(TAG, "onActivityBackPressed - illegalState: " + stateException);
        }
    }

    @Subscribe
    public void dispatchBackKeyEvent(BackKeyEvent event) {
        try {
            BaseActivity act = getActivity();
            if (act.isForegroundState()) {
                act.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
            }
        } catch (NullActivityException ignore) {
        }
    }
}// end of class

