package com.example.rbgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import com.example.*;
public class viewChoose extends View implements OnTouchListener{
	private float starttouchX, starttouchY;
	private int shiftY, deltaY;
	private int vieww, viewh;
	private int iconsize;
	private int isdown;
	public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					float pX,pY;
					switch (event.getAction()){
					case MotionEvent.ACTION_DOWN:
						pX = event.getX();  pY = event.getY();
						starttouchX = pX; starttouchY = pY;
						isdown = 1;
						System.out.println(pX);
						invalidate();
						return true;
						
					case MotionEvent.ACTION_MOVE:
						pX = event.getX();  pY = event.getY();
						//System.out.println(pY-starttouchY);
						if (Math.abs(pX-starttouchX)+Math.abs(pY-starttouchY)> 20) isdown = 0;
						move_to(pY-starttouchY);
						invalidate();
					break;
					case MotionEvent.ACTION_UP:
						pX = event.getX();  pY = event.getY();
						move_to(pY-starttouchY);
						freshydel();
						if (Math.abs(pX-starttouchX)+Math.abs(pY-starttouchY)> 40) isdown = 0;
						if (isdown!=0)
							check_and_go((float) 0.5*(pX+starttouchX),(float) 0.5*(pY+starttouchY));
						isdown = 0;
						invalidate();
					break;
					}
					return false;
				}
	    private void check_and_go(float x,float y) {
	    	for (int i = 0 ; i <=get_todo() && i<get_count(); i++){

            	int dx = i % get_column() * iconsize+iconsize/2;
            	int dy = i / get_column() * iconsize + shiftY+deltaY+iconsize/2;
            	if (Math.abs(x-dx)+Math.abs(y-dy) < iconsize *4/5) {
            		//Go to play;
            		Bundle bundle = (((Activity) this.getContext()).getIntent().getExtras());
            		Bundle newBundle = new Bundle();
            		newBundle.putString("school", bundle.getString("school"));
            		newBundle.putString("ID", bundle.getString("ID"));
            		newBundle.putString("IP", bundle.getString("IP"));
            		newBundle.putString("port", bundle.getString("port"));
            		newBundle.putInt("toPlay", i+1);
            		
            		Intent intent = new Intent();
            		intent.setClass(((Activity) this.getContext()),Game.class);
            		intent.putExtras(newBundle);
            		((Activity) this.getContext()).startActivity(intent);
            		((Activity) this.getContext()).finish();
            	}
	    	}
	}
		private int count;
	    private int column;
	    private int max_row;
	    private Paint font;
	    private int maxplayed;
	    private int get_todo(){
	       return maxplayed;
	    }
	    private int get_count() {
	    	return count = 100;
	    }
	    private int get_column(){
	       column = 6;
	       max_row = get_count() / column;
	       if (get_count() % column !=0) max_row ++;
	       return column;
	    }
	    private Bitmap bitmap1=null, bitmap2=null, bitmap3=null;
		private void mydetails(){
			setOnTouchListener(this);
			//canvas.drawBitmap(bitmap, (int) starttouchX,(int) starttouchY, null);
			shiftY = 0; deltaY = 0;
			Levelchecker ck = new Levelchecker((Activity) this.getContext());
			maxplayed = ck.get_maxplayed();
		} 
		public viewChoose(Context context) {
			super(context);	 
			 mydetails();
			 
		}
	    public viewChoose(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        mydetails();
	   }

		public viewChoose(Context context, AttributeSet attrs, int defStyle) {
	    	super(context, attrs, defStyle);
	    	mydetails();
	    }
		private void freshydel(){
			deltaY += shiftY;
			shiftY = 0;
		}
		private void move_to(float dd){
			//System.out.println(dd);
			if (deltaY+(int) dd > 0) return;
			if (deltaY+(int)dd + iconsize * max_row < viewh * 0.8 ) return;
			shiftY = (int) (dd);
			//System.out.println("!!"+String.valueOf(shiftY));
		}
		private void load_pic(){

			vieww =this.getWidth();
			viewh =this.getHeight();
			
			get_count();
			iconsize = vieww / get_column();	
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.lock1);
			int oldwidth = bmp.getWidth();
			float ratio = (float) iconsize / oldwidth;
            Matrix matrix = new Matrix(); matrix.postScale(ratio, ratio);
            bitmap1 = Bitmap.createBitmap(bmp,0,0,oldwidth,oldwidth,matrix,true);
            bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.lock2),0,0,oldwidth,oldwidth,matrix,true);
            bitmap3 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.lock3),0,0,oldwidth,oldwidth,matrix,true);
            font = new Paint();
            font.setColor(Color.RED);
            font.setTextSize(iconsize/3);
		}
		private int x;
		@SuppressLint("DrawAllocation")
		protected void onDraw(Canvas canvas) {  
            super.onDraw(canvas);  
            if (bitmap1 ==null) load_pic();
            x+=1;  
            Paint mPaint = new Paint();  
            //mPaint.setColor(Color.BLUE);  
            //canvas.drawRect(x, 40, (int) starttouchX+40, 80, mPaint);  
            //System.out.println("S"+String.valueOf(shiftY+deltaY));
            for (int i = 0 ; i < get_count(); i++){
            	int dx = i % get_column() * iconsize;
            	int dy = i / get_column() * iconsize + shiftY+deltaY;
            	if (dy <-iconsize) continue;
            	if (dy>viewh) continue;
            	if (i<get_todo())
            	canvas.drawBitmap(bitmap1, dx, dy,mPaint);
            	else if (i==get_todo())
            	canvas.drawBitmap(bitmap2, dx, dy,mPaint);
            	else 
            	canvas.drawBitmap(bitmap3, dx, dy,mPaint);
            	
            	canvas.drawText(String.valueOf(i+1), dx+iconsize*5/8-String.valueOf(i).length()*iconsize/8, dy+iconsize*5/8, font);
            	
            }
            
        }  
		
}
