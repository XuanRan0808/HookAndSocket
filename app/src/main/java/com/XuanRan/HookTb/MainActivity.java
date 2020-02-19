package com.XuanRan.HookTb;
 
import android.*;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.*;

public class MainActivity extends Activity 
{
    Switch mSwitch;
    SharedPreferences Shared;
    SharedPreferences.Editor ShEd;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwitch=findViewById(R.id.activity_mainSwitch);
        
        Shared=getSharedPreferences("Settings",0);
        //设置按钮状态
        mSwitch.setChecked(Shared.getBoolean("Switch",false));
        //得到编辑对象
        ShEd=Shared.edit();
        //按钮改变监听
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton p1, boolean p2)
                {
                    if(p2){
                        Toast.makeText(MainActivity.this,"状态：已启用",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this,"状态：已关闭",Toast.LENGTH_LONG).show();
               
                    }
                    ShEd.putBoolean("Switch",p2);
                    ShEd.commit();
                }
            });
    }
}
