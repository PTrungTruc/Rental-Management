package com.example.rental_management.others;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.example.rental_management.views.Login;

public class SessionManager {
    SharedPreferences pref;
    Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_UID = "uid";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_GROUP = "groupId";

    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String uid, String phone, String group) {
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_UID, uid);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_GROUP, group);
        editor.commit();
    }

    public void checkLogin() {
        if (!pref.getBoolean(IS_LOGIN, false)) {
            Intent i = new Intent(context, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    public String getUID() {
        return pref.getString(KEY_UID, null);
    }

    public String getUserPhone() {
        return pref.getString(KEY_PHONE, null);
    }

    public String getGroupId() {
        return pref.getString(KEY_GROUP, null);
    }

    public void setGroupId(String group){
        editor.putString(KEY_GROUP, group);
        editor.commit();
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}