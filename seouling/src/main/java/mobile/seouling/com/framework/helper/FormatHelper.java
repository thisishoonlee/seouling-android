package mobile.seouling.com.framework.helper;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import mobile.seouling.com.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.text.format.DateUtils.*;

@Deprecated
public class FormatHelper {

    private static final String TAG = "FormatHelper";

    public static final String YYYY_MM_DD_HH_MM_SS_Z = "yyyy-MM-dd HH:mm:ss Z";
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_Z);
        }
    };

    private static final SimpleDateFormat ISO8601 =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);

    private static final SimpleDateFormat PLAYBACK_TIME = new SimpleDateFormat("m:ss", Locale.getDefault());

    private FormatHelper() {
    }

    public static String format(Date date) {
        return DATE_FORMAT.get().format(date);
    }

    // convert time string to time.
    // ex) 2012-12-03 15:20:01 +0900
    public static String getTimeAgoString(Context context, String timeStr) {
        if (TextUtils.isEmpty(timeStr)) { return ""; }
        Date date;
        try {
            date = DATE_FORMAT.get().parse(timeStr);
        } catch (ParseException e) {
            return getTimeAgoString(context, replaceUTCToGMT(timeStr));
        }
        return getTimeAgoString(context, date);
    }

    private static String getTimeAgoString(Context context, Date date) {
        return getTimeAgoString(context, date.getTime());
    }

    public static String getTimeAgoString(Context context, long time) {
        long currentTime = System.currentTimeMillis();
        CharSequence timeAgoStr;
        long elapsedTime = currentTime - time;
        if (elapsedTime < MINUTE_IN_MILLIS) {
            return context.getString(R.string.just_now);
        } else if (elapsedTime < DAY_IN_MILLIS) {
            timeAgoStr = getRelativeTimeSpanString(time);
        } else if (elapsedTime < DAY_IN_MILLIS * 60){
            timeAgoStr = getRelativeDateTimeString(context, time, DAY_IN_MILLIS, WEEK_IN_MILLIS, 0);
        } else {
            timeAgoStr = formatDateTime(context, time, FORMAT_SHOW_DATE);
        }
        String string = timeAgoStr.toString();
        if (string.contains("/")) {
            string = string.replaceAll("/", ". ");
        } else {
            int index = string.lastIndexOf(",");
            if (index > 0) {
                string = string.substring(0, index);
            }
        }
        return string;
    }

    public static String getTimeString(long time) {
        return DATE_FORMAT.get().format(time);
    }

    public static Date getDate(String time_str) {
        try {
            return DATE_FORMAT.get().parse(time_str);
        } catch (ParseException e) {
            time_str = replaceUTCToGMT(time_str);
            if (time_str != null) {
                try {
                    return DATE_FORMAT.get().parse(time_str);
                } catch (ParseException ignored) {
                }
            }
            return null;
        }
    }

    @Nullable
    private static String replaceUTCToGMT(String s) {
        // It seems that Gingerbread cannot parse UTC timezone.
        int index = s.indexOf("UTC");
        if (index != -1) {
            return s.substring(0, index) + "GMT" + s.substring(index + 3);
        } else {
            return null;
        }
    }

    public static String getFormattedNumberString(long number) {
        return NumberFormat.getInstance().format(number);
    }

    private static final long UNIT_K = 1000;
    private static final long UNIT_M = 1000000;
    private static final long UNIT_B = 1000000000;

    public static String getNumberStringWithUnit(long number) {
        if (number >= UNIT_B) {
            return String.format("%.1fB", (float) number / UNIT_B);
        } else if (number >= UNIT_M * 100) {
            return Long.toString(number / UNIT_M) + "M";
        } else if (number >= UNIT_M) {
            return String.format("%.1fM", (float) number / UNIT_M);
        } else if (number >= UNIT_K * 100) {
            return Long.toString(number / UNIT_K) + "K";
        } else if (number >= UNIT_K * 10) {
            return String.format("%.1fK", (float) number / UNIT_K);
        } else if (number >= UNIT_K) {
            return String.format("%,d", number);
        } else {
            return Long.toString(number);
        }
    }

    private static Calendar sPlaybackCalendar;

    public static String toPlaybackTime(int msec) {
        if (sPlaybackCalendar == null) {
            sPlaybackCalendar = GregorianCalendar.getInstance();
        }
        sPlaybackCalendar.clear();
        sPlaybackCalendar.set(Calendar.MILLISECOND, msec);
        return PLAYBACK_TIME.format(sPlaybackCalendar.getTime());
    }

    public static String toISO8601(long time) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(time);
        return ISO8601.format(calendar.getTime());
    }
}// end of class
