package com.jdn.videostatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Utils {
    static SharedPreferences prefs;

    @SuppressLint("WrongConstant")
    public static boolean appInstalledOrNot(Context context, String uri) {
        try {
            context.getPackageManager().getPackageInfo(uri, 1);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static SharedPreferences getSharedPreference(Context context) {
        return prefs != null ? prefs : context.getSharedPreferences(Constants.APP_PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    public static String getPreference(Context context, String key) {
        return getSharedPreference(context).getString(key, "");
    }

    public static void setPreference(Context context, String key, String value) {
        getSharedPreference(context).edit().putString(key, value).apply();
    }

    public static int getIntPreference(Context context, String key) {
        return getSharedPreference(context).getInt(key, 0);
    }

    public static void setIntPreference(Context context, String key, int value) {
        getSharedPreference(context).edit().putInt(key, value).apply();
    }
}
