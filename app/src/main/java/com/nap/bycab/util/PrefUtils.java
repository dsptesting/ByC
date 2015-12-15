package com.nap.bycab.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nap.bycab.models.Driver;

public class PrefUtils {

    public static void setCurrentDriver(Driver currentDriver, Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "Driver_prefs", 0);
        complexPreferences.putObject("current_Driver_value", currentDriver);
        complexPreferences.commit();
    }

    public static void clearCurrentDriver( Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "Driver_prefs", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }

    public static Driver getCurrentDriver(Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "Driver_prefs", 0);
        Driver currentDriver = complexPreferences.getObject("current_Driver_value", Driver.class);
        return currentDriver;
    }



//    public static void setForm(GetAssignedForm1 currentDriver, Context ctx){
//        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "form1_prefs", 0);
//        complexPreferences.putObject("from1_value", currentDriver);
//        complexPreferences.commit();
//    }
//
//
//
//    public static GetAssignedForm1 getForm(Context ctx){
//        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "form1_prefs", 0);
//        GetAssignedForm1 currentDriver = complexPreferences.getObject("from1_value", GetAssignedForm1.class);
//        return currentDriver;
//    }

    public static void setNotificationId(String login, Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("notification", login);
        editor.apply();
    }

    public static String getNotificationId(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString("notification", "");

    }
}
