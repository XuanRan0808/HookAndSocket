
import android.app.*;
import android.os.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.*;
import java.io.*;
import java.net.*;
import android.widget.*;
import android.content.*;


/**
*
*    Created by XuanRan on 2020/02/18
*
*/
public class Main1 implements IXposedHookLoadPackage
{
    Socket socket;
    XC_LoadPackage.LoadPackageParam loadPackageParam;
    Activity activity;
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable
    {
        if (loadPackageParam.packageName.equals("com.xunmeng.pinduoduo"))
        {
            //类加载器
            this.loadPackageParam=loadPackageParam;
            // 开始Hook
            XposedHelpers.findAndHookMethod("com.xunmeng.pinduoduo.ui.activity.HomeActivity", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook(){


                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable
                    {
                        super.beforeHookedMethod(param);
                        activity=(Activity) param.thisObject;
                        //显示IP请求对话框
                        ShowIPAlertDialog();
                    }
                });

        }
    }
    public void ShowIPAlertDialog(){
        AlertDialog.Builder alertd=new AlertDialog.Builder(activity);
        alertd.setTitle("填写请求地址");
        alertd.setMessage("请填写要请求的IP地址：");
        alertd.setCancelable(false);
        final EditText ed=new EditText(activity);
        alertd.setView(ed);
        alertd.setPositiveButton("确定", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    getisHook(ed.getText().toString());
                }
            });
        alertd.setNegativeButton("取消",null);
        alertd.show();
        
    }
    public void getisHook(final String ipAddress)
    {
        new Thread() {

            @Override
            public void run()
            {
                //获取时间
                //long startTime = System.currentTimeMillis();
                sendHeartbeat();
                try
                {
                    socket = new Socket(ipAddress, 999);//IP地址和端口号
                    boolean flag=true;
                    while (flag)
                    {
                        InputStream inputStream = socket.getInputStream();
                        DataInputStream input = new DataInputStream(inputStream);
                        byte[] b = new byte[10000];
                        int length = input.read(b);
                        String Msg = new String(b, 0, length, "gb2312");
                        //如果消息不为空，则调用方法
                        if(Msg!=null){
                            startHook();
                        }
                        //关闭Socket连接
//                        socket.close();
//                        inputStream.close();
//                        input.close();
                    }

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    //重新启动
                    getisHook(ipAddress);
                }

            }
        }.start();
    }
    public void startHook(){
        //寻找HookClass
        Class<?> clazz=XposedHelpers.findClass("com.xunmeng.pinduoduo.secure.SecureNative", loadPackageParam.classLoader);
        //获取时间
        Long startTs =System.currentTimeMillis();
        //调用方法
        String Result=(String) XposedHelpers.callStaticMethod(clazz, "deviceInfo2", activity, startTs);
        //打印
        XposedBridge.log(Result);
    }
    public void sendSevice(String message){
        OutputStream os = null;
        try
        {

            os = socket.getOutputStream();//得到socket的输出流
            //输出EditText里面的数据，数据最后加上换行符才可以让服务器端的readline()停止阻塞
            os.write((message+ "\n").getBytes("utf-8"));
          
           }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            os.flush();
        }
        catch (IOException e)
        {
            //输出错误
            XposedBridge.log(e);
            //抛出错误
            //throw new RuntimeException("输出流刷新强制刷新失败" + e);
            //重新启动
            sendSevice(message);
        }
        
    }
    /*
    * 发送心跳包
    */
    public void sendHeartbeat()
    {
        try
        {
            new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        while (true)
                        {
                            try
                            {
                                Thread.sleep(10 * 1000);// 10s发送一次心跳
                                sendSevice("Heart...");//调用发送
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
