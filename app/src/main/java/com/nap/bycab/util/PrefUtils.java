package com.nap.bycab.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

    /*public static void setCurrentUser(User currentUser, Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        complexPreferences.putObject("current_user_value", currentUser);
        complexPreferences.commit();
    }



    public static User getCurrentUser(Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        User currentUser = complexPreferences.getObject("current_user_value", User.class);
        return currentUser;
    }


    public static void setNotificationId(String login, Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("notification", login);
        editor.apply();
    }
*/
//    public static String getNotificationId(Context ctx){
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
//        return preferences.getString("notification", "");
//
//    }
//
//
//    public static void setForm(GetAssignedForm1 currentUser, Context ctx){
//        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "form1_prefs", 0);
//        complexPreferences.putObject("from1_value", currentUser);
//        complexPreferences.commit();
//    }
//
//
//
//    public static GetAssignedForm1 getForm(Context ctx){
//        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "form1_prefs", 0);
//        GetAssignedForm1 currentUser = complexPreferences.getObject("from1_value", GetAssignedForm1.class);
//        return currentUser;
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
