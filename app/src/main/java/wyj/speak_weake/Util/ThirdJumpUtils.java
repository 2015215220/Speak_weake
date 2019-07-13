package wyj.speak_weake.Util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class ThirdJumpUtils {
/*
 * 实现第三方app跳转
 * */
    public static void Jump(Context context,String PackName){
        Intent intent;
        PackageManager packageManager = context.getPackageManager();
        intent = packageManager.getLaunchIntentForPackage(PackName);
        context.startActivity(intent);
    }

//    public static  void backHM(Context context){
//
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        ComponentName cn = new ComponentName("com.ryi.hmjj.activity", "MainActivity.class");
//        intent.setComponent(cn);
//        context.startActivity(intent);
//
//    }

    /** 通过包名去启动一个Activity*/
    public static   void openApp( Context context) {
        // TODO 把应用杀掉然后再启动，保证进入的是第一个页面
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage("wyj.speak_weake");//保证包名一样的
        PackageManager pManager = context.getApplicationContext().getPackageManager();
        List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent,
                0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String startappName = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;

            System.out.println( "启动的activity是: " + startappName+":"+className);

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(startappName, className);

            intent.setComponent(cn);
            context.getApplicationContext().startActivity(intent);
        }
    }
}
