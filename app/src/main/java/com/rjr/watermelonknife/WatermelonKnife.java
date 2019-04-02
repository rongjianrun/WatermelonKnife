package com.rjr.watermelonknife;

import android.app.Activity;

public class WatermelonKnife {

    public static void bind(Activity activity) {
        String cName = activity.getClass().getCanonicalName() + "$$ViewBinder";
        try {
            Class<?> clazz = Class.forName(cName);
            ViewBinder binder = (ViewBinder) clazz.newInstance();
            binder.bind(activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
