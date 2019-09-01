package mobile.seouling.com.application.data_provider;

import android.content.Context;
import android.content.SharedPreferences;
import mobile.seouling.com.application.BaseApplication;

public class BasePreference {

    private static final String TAG = "MallangPreference";
    private static final String FABRIC_LOG = "fabric_checked";
    private static final String VERIFY_CALL_CODE = "verify_call_code";
    private static final String PHONE_NUM = "phone_number";
    private static final String SIGN_UP_VERIFICATION_CODE = "sign_up_verification_code";
    private static final String SIGN_UP_NICKNAME = "sign_up_nickname";
    private static final String SIGN_UP_AGE = "sign_up_age";
    private static final String SIGN_UP_GENDER = "sign_up_gender";
    private static final String SIGN_UP_AREA = "sign_up_area";
    private static final String SIGN_UP_SUB_AREA = "sign_up_sub_area";
    private static final String SIGN_UP_USAGE_REASON = "sign_up_usage_reason";
    private static final String SIGN_UP_IDEAL_TYPE = "sign_up_ideal_type";
    private static final String SIGN_UP_SELF_INTRODUCTION = "sign_up_self_introduction";

    private static SharedPreferences sPref = null;

    private static SharedPreferences getInstance() {
        if (sPref == null) {
            synchronized (BasePreference.class) {
                if (sPref == null) {
                    sPref = BaseApplication.getContext().getSharedPreferences("mallangf", Context.MODE_PRIVATE);
                }
            }
        }
        return sPref;
    }

    public static void setFabricLog(boolean check) {
        getInstance().edit().putBoolean(FABRIC_LOG, check).apply();
    }

    public static boolean isFabricLogChecked() {
        return getInstance().getBoolean(FABRIC_LOG, false);
    }

    public static void setVerifyCallCode(String code) {
        getInstance().edit().putString(VERIFY_CALL_CODE, code).apply();
    }

    public static String getVerifyCallCode() {
        return getInstance().getString(VERIFY_CALL_CODE, "");
    }

    public static void setPhoneNumber(String phone) {
        getInstance().edit().putString(PHONE_NUM, phone).apply();
    }

    public static String getPhoneNum() {
        return getInstance().getString(PHONE_NUM, null);
    }

    public static void setVerificationCode(int code) {
        getInstance().edit().putInt(SIGN_UP_VERIFICATION_CODE, code).apply();
    }

    public static int getVerificationCode() {
        return getInstance().getInt(SIGN_UP_VERIFICATION_CODE, 0);
    }

    public static void setNickName(String nickName) {
        getInstance().edit().putString(SIGN_UP_NICKNAME, nickName).apply();
    }

    public static String getNickName() {
        return getInstance().getString(SIGN_UP_NICKNAME, "");
    }

    public static void setAge(int age) {
        getInstance().edit().putInt(SIGN_UP_AGE, age).apply();
    }

    public static int getAge() {
        return getInstance().getInt(SIGN_UP_AGE, 0);
    }

    public static void setGender(int gender) {
        getInstance().edit().putInt(SIGN_UP_GENDER, gender).apply();
    }

    public static int getGender() {
        return getInstance().getInt(SIGN_UP_GENDER, 0);
    }

    public static void setArea(int area) {
        getInstance().edit().putInt(SIGN_UP_AREA, area).apply();
    }

    public static int getArea() {
        return getInstance().getInt(SIGN_UP_AREA, 0);
    }

    public static void setSubArea(int area) {
        getInstance().edit().putInt(SIGN_UP_SUB_AREA, area).apply();
    }

    public static int getSubArea() {
        return getInstance().getInt(SIGN_UP_SUB_AREA, 0);
    }

    public static void setUsageReason(int reason) {
        getInstance().edit().putInt(SIGN_UP_USAGE_REASON, reason).apply();
    }

    public static int getUsageReason() {
        return getInstance().getInt(SIGN_UP_USAGE_REASON, 0);
    }

    public static void setIdealType(int ideal) {
        getInstance().edit().putInt(SIGN_UP_IDEAL_TYPE, ideal).apply();
    }

    public static int getIdealType() {
        return getInstance().getInt(SIGN_UP_IDEAL_TYPE, 0);
    }

    public static void setSelfIntroduction(String introduction) {
        getInstance().edit().putString(SIGN_UP_SELF_INTRODUCTION, introduction).apply();
    }

    public static String getSelfIntroduction() {
        return getInstance().getString(SIGN_UP_SELF_INTRODUCTION, "");
    }
}
