package com.example.rbgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class viewGame extends View {
	public viewGame(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.setOnTouchListener(new View.OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				float pX,pY;
				if (!mG.located) return false;
				switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
					pX = event.getX();  pY = event.getY();
					starttouchX = pX; starttouchY = pY;
					// TODO here
					dot_selected = mG.getIdByXY((int) pX, (int) pY, ballsize*ballsize*1.3f);
					if (dot_selected >=0 ){
						click_time_extended  = 0;
						click_time_extended_handler.postDelayed(set_click_time_extended, 100);
					}
					invalidate();
					return true;
					
				case MotionEvent.ACTION_MOVE:
					pX = event.getX();  pY = event.getY();
					if (dot_selected >=0){
						mG.setDotPosition(dot_selected, (int) (pX),(int) (pY));
					}
					// TODO here
					invalidate();
				break;
				case MotionEvent.ACTION_UP:
					// TODO here
					pX = event.getX();  pY = event.getY();
					if (dot_selected >=0){
						if (click_time_extended == 0){
							dot_to_delete=dot_selected;
							start_to_delete_dot(dot_to_delete);
						}
						//mG.setDotPosition(dot_selected, (int) (pX),(int) (pY));
					}
					// TODO here
					invalidate();
				break;
				}
				return false;
			}
		}
		);
	}
	private void start_to_delete_dot(int dot_to_delete){
		if (mG.matchleft ==0) {
			Toast.makeText(
					((Activity)this.getContext()),"No Matchs left, you have to UNDO~", Toast.LENGTH_LONG).show();
			return;
		}
		if (mG.deleteDot(dot_to_delete)) {
			upgrade();
		}
	}
	public void upgrade() {
		levelchecker = new Levelchecker((Activity)this.getContext());
		int newlevel = mG.level+1;
		if (mG.level == 100) {
			newlevel = mG.level;
			Toast.makeText(
					((Activity)this.getContext()), "Congratulations ~, don not forget to submit your results", Toast.LENGTH_LONG)
					.show();
		}
		else Toast.makeText(
				((Activity)this.getContext()), "Move to level "+String.valueOf(newlevel)+"   "+mG.uname, 
				Toast.LENGTH_LONG).show();
		if (levelchecker.get_maxplayed() == mG.level-1)
			levelchecker.levelup();
		mG = new Graph(newlevel, ((Activity)this.getContext()), mG.uname);
	}
	private Levelchecker levelchecker=null;
	private Graph mG=null;
	public void setmG(Graph G){
		mG = G;
	}
	
	private float starttouchX, starttouchY;
	private int dot_selected = - 1;
	private int dot_to_delete = -1;
	protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        if (!mG.located){
        	loadpictures();
        	mG.shuffleDots(ballsize, ballsize, vieww-ballsize, viewh-ballsize);
        }

        for (int i = 0 ; i < mG.size; i++)
        	if (mG.dotExist[i]) for (int j = i+1; j<mG.size; j++) if (mG.map[i][j]&&mG.dotExist[j])
        		canvas.drawLine(mG.dotX[i], mG.dotY[i], mG.dotX[j], mG.dotY[j], linepaint);
       
        for (int i = 0 ; i < mG.size; i++)
        	if (mG.dotExist[i]) {
        		if (i==dot_to_delete)
        		canvas.drawBitmap(bitmap1, mG.dotX[i] - ballsize/2,mG.dotY[i] - ballsize/2, font);
        		else 
        			canvas.drawBitmap(bitmap2, mG.dotX[i] - ballsize/2,mG.dotY[i] - ballsize/2, font);
        		
        		canvas.drawText(String.valueOf(mG.deg[i]),  mG.dotX[i]-ballsize*3/16-String.valueOf(i).length()*ballsize/18, mG.dotY[i]+ballsize*2/8, font);
        	}
         
           
        mG.triggerForce(vieww);
        canvas.drawText("Matchs"+mG.matchleft, 50,50, font);
        //mG.scaleinto(vieww-ballsize, viewh-ballsize, ballsize/2, ballsize/2);
       // this.invalidate();
        
	}
	public boolean tryRoolback() {
		if (mG.delstack ==0) {
		    return false;
		}
		mG.undelDot();
		
		return true;
	}
	private int vieww,viewh;
	private int ballsize;
	private Bitmap bitmap1, bitmap2;
	private Paint font,linepaint;
	public int click_time_extended = 0;
	private void loadpictures(){
		vieww =this.getWidth();
		viewh =this.getHeight();
		
		ballsize = vieww / 10;	
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.dot1);
		int oldwidth = bmp.getWidth();
		float ratio = (float) ballsize / oldwidth;
        Matrix matrix = new Matrix(); matrix.postScale(ratio, ratio);
        bitmap1 = Bitmap.createBitmap(bmp,0,0,oldwidth,oldwidth,matrix,true);
        bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dot3),0,0,oldwidth,oldwidth,matrix,true);
        font = new Paint();
        linepaint = new Paint();
        linepaint.setColor(Color.BLUE);
        linepaint.setStrokeWidth((float) (vieww*0.006));
        font.setColor(Color.RED);
        font.setTextSize(ballsize*2/3);

	}
	Handler click_time_extended_handler = new Handler();
	private Runnable set_click_time_extended= new Runnable() {
		public void run(){
		click_time_extended = 1;
		}
	};
	
	

}
