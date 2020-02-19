package com.XuanRan.HookTb;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.*;
import android.os.*;
import de.robv.android.xposed.XC_MethodHook.*;
import android.content.*;
import android.app.*;
import android.widget.*;

/**
*
*    Created by XuanRan on 2020/02/17
*
*/
public class Main implements IXposedHookLoadPackage
{
    Context mContext;
    boolean isOpenCall;
    String Result;
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam p1) throws Throwable
    {
        

       if(p1.packageName.equals("com.xunmeng.pinduoduo")){
           
           XposedHelpers.findAndHookMethod("com.xunmeng.pinduoduo.ui.activity.HomeActivity",p1.classLoader,"onCreate",Bundle.class,new XC_MethodHook(){
               

                   @Override
                   protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable
                   {
                       super.beforeHookedMethod(param);
                       Activity activity=(Activity) param.thisObject;
                       Class<?> clazz=XposedHelpers.findClass("com.xunmeng.pinduoduo.secure.SecureNative",p1.classLoader);

                       Result=(String) XposedHelpers.callStaticMethod(clazz,"deviceInfo2",activity,null);
                       
                   }
           });
       }
        
        
    }
    public class AddToast extends XC_MethodHook
    {

        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable
        {
            super.beforeHookedMethod(param);
            Activity activity=(Activity) param.thisObject;
            Toast.makeText(activity,"当前状态"+Result,Toast.LENGTH_LONG).show();
            
        }
        
        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable
        {
            super.afterHookedMethod(param);
            Activity activity=(Activity) param.thisObject;
            Toast.makeText(activity,"当前状态"+Result,Toast.LENGTH_LONG).show();
        }
        
    }
    /*
     *  对话框添加方法
     */
    public class addAlertDialog extends XC_MethodHook
    {

        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable
        {
            super.beforeHookedMethod(param);
            mContext=(Context) param.thisObject;
            ShowAlertDialog();
        }

        private void ShowAlertDialog()
        {
            AlertDialog.Builder alert=new AlertDialog.Builder(mContext);
            alert.setTitle("执行Hook");
            alert.setMessage("要开始执行Hook逻辑吗");
            alert.setCancelable(false);
            alert.setPositiveButton("执行", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        /*
                         *  调用方法
                         *参数一为Class类对象
                         *参数二为方法名
                         *参数三为传入参数
                         *参数四也为传入参数
                         */
                      
                    }
                });
            alert.setNegativeButton("取消",null);
            alert.show();
        }
        
    }
}
