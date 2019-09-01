package mobile.seouling.com.framework.util;


import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import mobile.seouling.com.application.BaseApplication;

public class DipUnit {

    public static int toPixels(Context context, float dip) {
        Resources r = context.getResources();
        float pix = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return (int) pix;
    }

    public static int toPixels(float dip) {
        return toPixels(BaseApplication.getContext(), dip);
    }
}
