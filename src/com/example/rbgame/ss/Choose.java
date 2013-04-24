package com.example.rbgame;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.app.Activity;
import android.content.Intent;
public class Choose extends Activity {


    private viewChoose mview;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_choose);
        mview = (viewChoose) findViewById(R.id.viewChoose1);
        //this.setContentView(this.mview);
        final Handler mHandler = new Handler();
        //调用Handler.post(Runnable r)方法
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //直接调用View.invalidate()，更新组件
                mview.invalidate();
                //延迟50毫秒后执行线程
                mHandler.postDelayed(this, 50);
            }
        });

    }
    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() ==0)
        {

            Intent intent = new Intent();
            intent.setClass(Choose.this, Welcome.class);
            startActivity(intent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}