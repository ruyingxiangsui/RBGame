package com.example.rbgame;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Help extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		Button mReturn = (Button) findViewById(R.id.buttonReturnfromhelp);
		
		
		mhandler = new MyHandler();
		
		
		mReturn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Help.this.finish();
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}
	

	public boolean onKeyDown(int keyCode,KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() ==0)
		{
			new Thread(new myHTTPposter()).start();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private class myHTTPposter implements Runnable{

		@Override
		public void run() {

			String url = "http://218.241.236.109:8080/save.php";
			 
			HttpPost httpRequest = new HttpPost(url);
			ArrayList <NameValuePair> paras = new ArrayList<NameValuePair>();
			paras.add(new BasicNameValuePair("name","sdyy"));
			paras.add(new BasicNameValuePair("text","asdf"));
			try{
				httpRequest.setEntity(new UrlEncodedFormEntity(paras,HTTP.UTF_8));
				BasicHttpParams hpms = new BasicHttpParams();
				DefaultHttpClient dfh = new DefaultHttpClient(hpms);
				dfh.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				dfh.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 11000);
				
				HttpResponse response = dfh.execute(httpRequest);
				mhandler.obtainMessage(response.getStatusLine().getStatusCode()).sendToTarget();
				 
			}catch (ClientProtocolException e){
				
			}catch (IOException e) {
				e.printStackTrace();
				
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	private MyHandler mhandler;
	private myHTTPposter mposter;
	private class MyHandler extends Handler{
		public void dispatchMessage (Message msg){
			System.out.println(msg.what);
		}
	}

}
