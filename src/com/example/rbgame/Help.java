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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Help extends Activity {
	private int loaded_picture = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		final ImageView mImageView = (ImageView) findViewById(R.id.imageView1);
		loaded_picture = 1;
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.p1);
		mImageView.setImageBitmap(bmp);
		
		mImageView.setOnClickListener(new ImageView.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap bmp = null;
				switch (loaded_picture) {
				    case 1: 
						bmp = BitmapFactory.decodeResource(getResources(), R.drawable.p2);
						loaded_picture = 2;
						
						break;
				    case 2:bmp = BitmapFactory.decodeResource(getResources(), R.drawable.p3);
				        loaded_picture = 3;
				        break;
				    case 3:bmp = BitmapFactory.decodeResource(getResources(), R.drawable.p4);
				        loaded_picture = 4;
				        break;
				    case 4:
			            Help.this.finish();			        
					break;
				}
				mImageView.setImageBitmap(bmp);
			}
			
		});
	}

	

	public boolean onKeyDown(int keyCode,KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() ==0)
		{
			Help.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
