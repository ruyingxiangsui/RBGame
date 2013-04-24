package com.example.rbgame;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class Game extends Activity {

    Bundle bundle;
    String schoolname,ID;
    viewGame myGameview;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //TextView gt = (TextView) findViewById(R.id.gametextview);
        bundle = this.getIntent().getExtras();
        schoolname = bundle.getString("school");
        ID = bundle.getString("ID");
        int level = bundle.getInt("toPlay");
        Toast.makeText(Game.this, "Welcome to level "+
                       String.valueOf(level)+"! "+schoolname+":"+ID+"", Toast.LENGTH_LONG).show();

        myGameview = (viewGame) findViewById(R.id.viewGame1);
        myGameview.setmG(new Graph(level, Game.this,ID+schoolname));
        myGameview.postInvalidate();

        //this.setContentView(this.mview);
        final Handler mHandler = new Handler();
        //调用Handler.post(Runnable r)方法
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //直接调用View.invalidate()，更新组件
                myGameview.invalidate();
                //延迟50毫秒后执行线程
                mHandler.postDelayed(this, 50);
            }
        });

    }

    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() ==0)
        {
            if (myGameview.tryRoolback()) return true;
            new AlertDialog.Builder(Game.this).setTitle("确认退出").setMessage("push Back to resume").
            setPositiveButton("Quit", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClass(Game.this, Choose.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    Game.this.finish();
                }
            }
            ).setNegativeButton("Back", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            }).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
