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
		fireState = 0;
		this.setOnTouchListener(new View.OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				float pX,pY;
				if (!mG.located) return false;
				if (fireState !=0) return false;
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
	private int rmvDot;
	private void start_to_delete_dot(int dot_to_delete){
		if (mG.matchleft ==0) {
			Toast.makeText(
					((Activity)this.getContext()),"No Matchs left, you have to UNDO~", Toast.LENGTH_LONG).show();
			return;
		}
		fireState = 1;
		rmvDot = dot_to_delete;
		matchX = (float)  mG.matchleft * matchsize;
		matchY = 0.0f;

	    float dx = (float) (1.0f* mG.dotX[dot_to_delete] - (matchX+matchsize));
	    float dy = 1.0f* mG.dotY[dot_to_delete] - matchsize*0.5f;
	    float ddl =  (float) Math.sqrt(dx*dx+dy*dy);
	    dx /= ddl;
	    dy /= ddl;
	    matchFlydX =  (vieww*0.05f * dx);
	    matchFlydY = (vieww*0.05f * dy);
	    
		//TODO  :: remove this:!
	}
	public float matchX,matchY;
	public float matchFlydX, matchFlydY;
	public int fireState = 0;
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

         if (fireState ==0)
        mG.triggerForce(vieww);
        //canvas.drawText("Matchs"+mG.matchleft, 50,50, font);
        for (int i = 0 ; i < mG.matchleft; i++) {
        	if (i==mG.matchleft-1 && fireState !=0) continue;
        	canvas.drawBitmap(bitmapmatch, i*matchsize,0,font);
        	
        }
        //mG.scaleinto(vieww-ballsize, viewh-ballsize, ballsize/2, ballsize/2);
       // this.invalidate();
       if (fireState == 1) {
    	   
    	   matchX += matchFlydX;
    	   matchY += matchFlydY;
    	   
    	   float dx = (float) (1.0f* mG.dotX[rmvDot] - (matchX+matchsize));
   	       float dy = 1.0f* mG.dotY[rmvDot]- (matchY+matchsize*0.5f);
   	   
    	   if (dx*dx+dy*dy < vieww*vieww*0.05f*0.05f)
    		   fireState =2;
    	   canvas.drawBitmap(bitmapmatch, matchX,matchY,font);
       }
       if (fireState >= 2){
    	   fireState ++;
    	   for (int i = 0 ; i < mG.size; i++)
    		   if (mG.dotExist[i] && mG.map[i][rmvDot]) {
    			   int px = (int) (mG.dotX[i] * fireState + mG.dotX[rmvDot] *(MAXFIRESTATE-fireState))/MAXFIRESTATE;
    			   int py = (int) (mG.dotY[i] * fireState + mG.dotY[rmvDot] *(MAXFIRESTATE-fireState))/MAXFIRESTATE;
    			   canvas.drawLine(mG.dotX[i], mG.dotY[i], px, py, linepaint);
    			   if (fireState % 2 == 0) 
    				   canvas.drawBitmap(fire1, px - firewidth/2 , py - fireheight, font);
    			   else canvas.drawBitmap(fire2, px - firewidth/2 , py - fireheight, font);
    		   } 
       }
       

       for (int i = 0 ; i < mG.size; i++)
       	if (mG.dotExist[i]) for (int j = i+1; j<mG.size; j++) if (mG.map[i][j]&&mG.dotExist[j]) {
       		if (fireState >=2 && (rmvDot==i || rmvDot ==j)) continue;
       		canvas.drawLine(mG.dotX[i], mG.dotY[i], mG.dotX[j], mG.dotY[j],linepaint);
       	}
       
      
       for (int i = 0 ; i < mG.size; i++)
       	if (mG.dotExist[i]) {
       		if (i==dot_to_delete)
       		canvas.drawBitmap(bitmap1, mG.dotX[i] - ballsize/2,mG.dotY[i] - ballsize/2, font);
       		else 
       			canvas.drawBitmap(bitmap2, mG.dotX[i] - ballsize/2,mG.dotY[i] - ballsize/2, font);
       		
       		canvas.drawText(String.valueOf(mG.deg[i]),  mG.dotX[i]-ballsize*3/16-String.valueOf(i).length()*ballsize/18, mG.dotY[i]+ballsize*2/8, font);
       	}
       if (fireState == MAXFIRESTATE) {
    	   fireState = 0;
		   if (mG.deleteDot(rmvDot)) {
   			upgrade();
   		}
       }
      
	}
	public static int MAXFIRESTATE = 30;
	public boolean tryRoolback() {
		if (mG.delstack ==0) {
		    return false;
		}
		mG.undelDot();
		
		return true;
	}
	private int vieww,viewh;
	private int ballsize;
	private int matchsize;
	private Bitmap bitmap1, bitmap2;
	private Bitmap bitmapmatch;
	private Paint font,linepaint;
	private Bitmap fire1,fire2;
	private int firewidth,fireheight;
	public int click_time_extended = 0;
	private void loadpictures(){
		vieww =this.getWidth();
		viewh =this.getHeight();
		
		ballsize = vieww / 10;	
		matchsize =vieww / 12;
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.yellowstar);
		int oldwidth = bmp.getWidth();
		float ratio = (float) ballsize / oldwidth;
        Matrix matrix = new Matrix(); matrix.postScale(ratio, ratio);
        bitmap1 = Bitmap.createBitmap(bmp,0,0,oldwidth,oldwidth,matrix,true);
        bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.redstar),0,0,oldwidth,oldwidth,matrix,true);
        
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.match);
        oldwidth = bmp.getWidth();
        ratio = (float) matchsize / oldwidth;
        matrix = new Matrix(); matrix.postScale(ratio, ratio);
        bitmapmatch = Bitmap.createBitmap(bmp,0,0,oldwidth,oldwidth,matrix,true);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fire1);
        matrix = new Matrix(); matrix.postScale(((float) matchsize)/bmp.getWidth(),((float) matchsize)/bmp.getWidth());
        fire1 = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
        fire2 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fire2),0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
        firewidth = fire1.getWidth();
        fireheight = fire1.getHeight();
        
        font = new Paint();
        linepaint = new Paint();
        linepaint.setColor(Color.BLUE);
        linepaint.setStrokeWidth((float) (vieww*0.006));
        font.setColor(Color.YELLOW);
        font.setTextSize(ballsize*2/3);

	}
	Handler click_time_extended_handler = new Handler();
	private Runnable set_click_time_extended= new Runnable() {
		public void run(){
		click_time_extended = 1;
		}
	};
	
	

}
