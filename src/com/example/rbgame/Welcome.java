package com.example.rbgame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Welcome extends Activity {
	private  EditText mTextSchool;
	private  EditText mTextID;
	private  EditText mTextIP;
	private  EditText mTextport;
	private  int toUploadCount ;
	private Button buttonUpload; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		mhandler= new MyHandler();
		
		mTextSchool = (EditText) findViewById(R.id.editschool);
		mTextID = (EditText) findViewById(R.id.textID);
		mTextIP = (EditText) findViewById(R.id.textIP);
		mTextport = (EditText) findViewById(R.id.textPort);

		Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
		Button buttonHelp = (Button) findViewById(R.id.buttonHelp);
		buttonUpload = (Button ) findViewById(R.id.buttonUpload);
		
		toUploadCount = countfile();
		if (toUploadCount!=0)
			Toast.makeText(Welcome.this, "找到"+String.valueOf(toUploadCount)+"条记录,请上传", Toast.LENGTH_SHORT).show();
		buttonUpload.setEnabled(toUploadCount!=0);
		try {
			FileInputStream is = this.openFileInput("login.txt");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			mTextSchool.setText(br.readLine());
			mTextID.setText(br.readLine());
			mTextIP.setText(br.readLine());
			mTextport.setText(br.readLine());
			is.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			mTextSchool.setText("BUAA");
			mTextID.setText("TEST");
			mTextIP.setText("AUTO");
			mTextport.setText("AUTO");
		}
		
		
		buttonPlay.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (mTextID.getText().toString().contains(".") || mTextSchool.getText().toString().contains("."))
				{
					new AlertDialog.Builder(Welcome.this).setTitle("ERROR")
					.setMessage("Invalid school/ID")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					}).show();
					return;
				}
				Intent intent = new Intent();
				intent.setClass(Welcome.this, Choose.class);
				Bundle bundle = new Bundle();
				bundle.putString("school", mTextSchool.getText().toString());
				bundle.putString("ID", mTextID.getText().toString());
				if (mTextIP.getText().toString().equals("AUTO"))
					bundle.putString("IP", "218.241.236.109");
				else bundle.putString("IP", mTextIP.getText().toString());
				if (mTextport.getText().toString().equals("AUTO"))
					bundle.putString("port", "8080");
				else 
				bundle.putString("port", mTextport.getText().toString());
				save_instance();
				intent.putExtras(bundle);
				startActivity(intent);
				Welcome.this.finish();
			}
			
		});
		
		
		buttonHelp.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(Welcome.this, Help.class);
				startActivity(intent);
			}
			
		});
		
		buttonUpload.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// start to upload
				save_instance();
				startUpload();
				
			}
			
		});
		
		
		
	}
	private String uploadstr;
	private void startUpload(){

		String [] flist = this.fileList();
		for (int i = 0 ; i < flist.length; i++)
			System.out.println(flist[i]);
		uploadstr = new String();
		for (int i = 0 ; i < flist.length; i++)
		if (flist[i].startsWith("S")){
			FileInputStream is;
			try {
				is = this.openFileInput(flist[i]);
		//		System.out.println(flist[i]+"::");
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				uploadstr+= br.readLine()+"\n";
			} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (uploadstr.length()<=0) {
			Toast.makeText(Welcome.this, "未发现需要上传的记录", Toast.LENGTH_LONG).show();
			return ;
		}
		new Thread(new myHTTPposter()).start();

		progressDialog = new ProgressDialog(Welcome.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Uploading .... ");
		progressDialog.setCancelable(false);
		progressDialog.show();
		
	}
	ProgressDialog progressDialog;
	
	protected void save_instance()  {
		try {
		FileOutputStream os = this.openFileOutput("login.txt",MODE_PRIVATE);
		
		
		BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os));
		
			br.write(mTextSchool.getText().toString()+"\n");
		br.write(mTextID.getText().toString()+"\n");
		br.write(mTextIP.getText().toString()+"\n");
		br.write(mTextport.getText().toString()+"\n");
		br.close();
		os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public void checkAndUpLoad() {
		String [] flist = this.fileList();
		for (int i = 0 ; i < flist.length; i++)
			System.out.println(flist[i]);
		for (int i = 0 ; i < flist.length; i++){
			FileInputStream is;
			try {
				is = this.openFileInput(flist[i]);
				System.out.println(flist[i]+"::");
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				System.out.println(br.readLine());
			} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		}
			
	}
	public int countfile(){
		int ans = 0;
		String [] flist = this.fileList();
		for (int i = 0 ; i < flist.length; i++)
			if (flist[i].startsWith("S")) ans++;
		return ans;
		
	}
	
	private myHTTPposter mhttpposter;
	private class myHTTPposter implements Runnable{

		@Override
		public void run() {
			isOK = new String();
			String server = mTextIP.getText().toString();
			String port = mTextport.getText().toString();
			if (server.equals("AUTO")) server = "218.241.236.109";
			if (port.equals("AUTO")) port = "8080";
			String url = "http://"+server+":"+port+"/save.php";
			 
			HttpPost httpRequest = new HttpPost(url);
			ArrayList <NameValuePair> paras = new ArrayList<NameValuePair>();
			String filename = ((new SimpleDateFormat("MMddHHmmss")).format(new java.util.Date()))
					+String.valueOf((new Random()).nextFloat());
			
			paras.add(new BasicNameValuePair("name",filename));
			paras.add(new BasicNameValuePair("text",uploadstr));
			try{
				httpRequest.setEntity(new UrlEncodedFormEntity(paras,HTTP.UTF_8));
				BasicHttpParams hpms = new BasicHttpParams();
				DefaultHttpClient dfh = new DefaultHttpClient(hpms);
				dfh.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				dfh.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 11000);
				
				HttpResponse response = dfh.execute(httpRequest);
				InputStream is = response.getEntity().getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				isOK = br.readLine();
				mhandler.obtainMessage(response.getStatusLine().getStatusCode()).sendToTarget();
				
			}catch (ClientProtocolException e){
				mhandler.obtainMessage(-1).sendToTarget();
			}catch (IOException e) {
				mhandler.obtainMessage(-2).sendToTarget();			
			}catch (Exception e) {
				mhandler.obtainMessage(-3).sendToTarget();
			}
		}
		
	}
	private String isOK = null;
	MyHandler mhandler;
	private class MyHandler extends Handler{
		public void dispatchMessage (Message msg){
			progressDialog.dismiss();
			progressDialog = null;
			System.out.println(msg.what);
			System.out.println(isOK);
			Toast.makeText(Welcome.this,isOK,Toast.LENGTH_LONG).show();
			if (isOK.equals("OK") && msg.what == 200) {
				isOK=null;
				(new AlertDialog.Builder(Welcome.this)).setTitle("上传成功")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
				String [] flist = Welcome.this.fileList();
					uploadstr = new String();
					for (int i = 0 ; i < flist.length; i++)
						if (flist[i].startsWith("S")) 
							Welcome.this.deleteFile(flist[i]);
					buttonUpload.setEnabled(false); 
				return;
			}
			if (msg.what >=0){
				Toast.makeText(Welcome.this,"Fail to upload",Toast.LENGTH_SHORT).show();
				isOK=null;
			}
			if (msg.what == -1) {
				Toast.makeText(Welcome.this,"Fail to upload:ClientProtocolException",Toast.LENGTH_SHORT).show();
			}else if (msg.what == -2) {
				Toast.makeText(Welcome.this,"Fail to upload:Please check the network",Toast.LENGTH_SHORT).show();
			}
			else if (msg.what == -3){
				Toast.makeText(Welcome.this,"Fail to upload:General Exception",Toast.LENGTH_SHORT).show();
			}
			return;
			
		}
	}


}
