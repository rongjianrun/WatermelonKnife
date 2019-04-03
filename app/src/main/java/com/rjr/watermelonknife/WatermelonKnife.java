package com.rjr.watermelonknife;

import android.app.Activity;

public class WatermelonKnife {

    public static void bind(Activity activity) {
        // 这里为什么要用反射？？？
        // 因为我们无法预知ViewBinder的实现类
        // 要根据类名动态创建实例
        // 换种说法，我们要创建的类还不存在，需要编译后才生成
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
