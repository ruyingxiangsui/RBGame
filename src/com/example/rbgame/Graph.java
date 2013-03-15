package com.example.rbgame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;

public class Graph {
	int size;
	int width;
	int height;
	int startx;
	int starty;
	public float dotX[];
	public float dotY[];
	public boolean dotExist[];
	public int deg[];
	public int delhistory[];
	public int delstack;
	public int deltogethernumber[];
	public int matchleft;
	public boolean map[][];
	Activity owner;
	Random random;
	public boolean located;
	public int timeCreated;
	public int level;
	String uname ;
	public  ArrayList<String> history; 
	//Histroy: Timecreated
	//action, time , id ::
	//C,gameid,time,UID :: create game
	//M,vid,x,y,time    :: move dot to
	//D,vid,time        :: delete V
	//U,time            :: undo delete
	public Graph(int gameid, Activity Owner,String Username){
		level = gameid;
		uname = Username;
		timeCreated = new Timer().getvalue();
		owner = Owner;
		located = false;
		history = new ArrayList<String>();
		history.add("C"+String.valueOf(gameid)+","+String.valueOf(timeCreated)+","+Username+","+String.valueOf(timeCreated));;
		String S = null;
		try {
		//	FileInputStream is = (FileInputStream) owner.getResources().getAssets().open("graph.txt");
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader(owner.getResources().getAssets().open("graph.txt")
			));
			for (int i = 1; i< gameid; i++) br.readLine();
			 S = br.readLine();
			br.close(); 

		} catch (IOException e) {
		}
			String[] iS = S.split(" ");
			size = Integer.parseInt(iS[0]);
			matchleft =Integer.parseInt(iS[1]);
			deg = new int [size];
			delhistory = new int [size];
			deltogethernumber = new int[size];
			dotExist = new boolean [size]; delstack = 0;
			
			map = new boolean[size][size];
			int ee = Integer.parseInt(iS[2]);
			for (int i = 0 ; i < size; i++) {
				dotExist[i] = true;
				for (int j = 0 ; j< size; j++) map[i][j] = false;
			}
			int qq = 0;
			for (int i = 0 ; i < ee; i++) {
				int x = Integer.parseInt(iS[3+i+i]);
				int y = Integer.parseInt(iS[4+i+i]);
				map[x][y] = map[y][x] = true;
				deg[x]++; deg[y]++;
				qq+= x+ y + deg[x];
			}
			random = new Random(qq);
		//read from the assets file;
	}
	
	
	public void setDotPosition(int id, int X, int Y) {
		dotX[id] = 1.0f*X;
		dotY[id] = 1.0f*Y;
		//history.add("M"+","+String.valueOf(id)+","+String.valueOf(X)+","+String.valueOf(Y)
			//	+","+String.valueOf((new Timer()).getvalue()-timeCreated));
	}
	
	public void shuffleDots(int X1,int Y1,int X2, int Y2) {
		width = X2 - X1;
		height = Y2 - Y1;
		startx = X1;
		starty = Y1;
		int best = size*size*size*size;
		float [] bX = null;
		float [] bY= null;
		for (int qq = 1; qq<=10; qq++) {

			dotX = new float [size];
			dotY = new float [size];
			for (int i = 0 ; i < size; i++) {
				dotX[i] = 1.0f* random.nextInt(width);
		    	dotY[i] = 1.0f* random.nextInt(height);
			}
			int nowcalc = calcIntersection();
			if (nowcalc < best) {
				best = nowcalc; bX = dotX; bY = dotY;
			}
		
		}
		dotX = bX; dotY = bY; 
		located = true;
		/*
		int time = 0 ;
		while ( locate(width/3,(float) (width),0.6f) > width*1.0) {
			time ++;
			if (time > 100) break;
		}*/
		for (int i = 0 ; i < 1000; i++) triggerForce(width);
		
		scaleinto(X2-X1,Y2-Y1,X1,Y1);
		located = true;
		
	}
	
	public float triggerForce(int bestlen){
		
		 float x =  locate(bestlen/3,(float) (bestlen*bestlen),0.03f);
		// scaleinto(width,height,startx,starty);
		 return x; 
	}
	
	 private void scaleinto(int width, int height,int STX,int STY) {
		float x1=21474831.0f, x2 = -x1;
		float y1= x1, y2 = x2;
		for (int i = 0 ; i < size; i++)if (dotExist[i]){
			if (dotX[i] < x1) x1 = dotX[i]; else if (dotX[i]>x2) x2 = dotX[i];
			if (dotY[i] < y1) y1 = dotY[i]; else if (dotY[i]>y2) y2 = dotY[i];
		}
		for (int i = 0 ; i < size; i++) {
			dotX[i] = STX+(dotX[i]-x1) * width / (x2-x1);
			dotY[i] = STY+(dotY[i]-y1) * height / (y2-y1);
		}
	}
	
	private float locate(int bestlength, float G, float switch_ratio) {
		//returns the total drift during one round of force;
		float [] dx; float [] dy;
		dx = new float[size]; dy = new float[size];
		for (int i = 0 ; i < size; i++) if (dotExist[i])
			for (int j = i+1 ; j < size; j++) if (dotExist[j]) {
				float ddx = dotX[j]- dotX[i];
				float ddy = dotY[j]- dotY[i];
				float sl = (float) Math.sqrt((double) ddx*ddx+ddy*ddy);
				float iddx = ddx/sl; float iddy = ddy/sl;
				//should add a line - point force;
				
				float sucks =  - G/(sl*sl);
				if (sl < bestlength*0.2f) sucks = sucks * (bestlength*0.2f/sl);
				if (map[i][j]) sucks += (sl - bestlength*1.0) * switch_ratio;
				if (sl < 1.0) {
					sucks = bestlength * 0.3f;
					iddx = random.nextFloat();
					iddy = random.nextFloat();
				}
				else if ( ddx < 10 && ddx > - 10) 
					iddx = random.nextFloat();
				else if (ddy < 10 && ddy > -10)
					iddy = random.nextFloat();
				dx[i] += iddx*sucks; dy[i]+=iddy*sucks;
				dx[j] -= iddx*sucks; dy[j]-=iddy*sucks;
			}

	   float tooclose =  (bestlength *0.2f); 
	   
	   float ans = (float) 0.0;
	   for (int i = 0 ; i < size; i++){
		   dotX[i] += (int) dx[i];
		   dotY[i] += (int) dy[i];
		   ans += Math.abs(dx[i])+Math.abs(dy[i]);
	   }
	   float minx = startx + tooclose;
	   float miny = starty + tooclose;
	   float maxx = startx+width - tooclose;
	   float maxy = starty +height - tooclose;
	   for (int i = 0 ; i < size; i++) {
		   if (dotX[i] <minx) dotX[i] =minx-(float) Math.log(minx- dotX[i]);
		   if  (dotY[i] <miny) dotY[i] =miny-(float) Math.log(miny- dotY[i]);
		   if (dotX[i] >maxx) dotX[i] =maxx+(float) Math.log( dotX[i]-maxx);
		   if  (dotY[i] >maxy) dotY[i] =maxy+(float) Math.log( dotY[i]-maxy);
		   
	   }
	   
	   /*
	    * for (int i = 0 ; i<size; i++) {
		   while (dotX[i] < startx+tooclose/2) dotX[i] = (dotX[i] + startx+tooclose)/2;
		   while (dotY[i] < starty+tooclose/2) dotY[i] = (dotY[i] + starty+tooclose)/2;
		   while (dotX[i] >startx+width-tooclose/2) dotX[i] = (dotX[i] +startx+width-tooclose)/2;
		   while (dotY[i] > starty+height-tooclose/2) dotY[i] =(dotY[i] +starty+height-tooclose)/2;
	   }
	   */
	   return ans;
	}
	
	private int calcIntersection() {
		int aa = 0;
		for (int i = 0 ; i < size; i++)
			for (int j = i+1; j<size; j++)
				for (int k = i+1; k<size; k++)
					for (int l = k+1; l < size; l++)
						if (intersection(i,j,k,l)) aa++;
		return aa;
	}

	private boolean intersection(int a, int b, int c, int d) {
		return inter(a,b,c,d) && inter (c,d,a,b);
	}

	private boolean inter(int a, int b, int c, int d) {
		return ((cross(a,b,c) * cross(a,b,d)) <0);
	}

	private int cross(int a, int b, int c) {
		float x1 = dotX[b]-dotX[a];
		float x2 = dotX[c]-dotX[a];
		float y1 = dotY[b]-dotY[a];
		float y2 = dotY[c]-dotY[a];
		float q = x1*y2-x2*y1;
		if (q>0.01) return 1; 
		if (q<-0.01) return -1; else return 0;
	}

	public boolean deleteDot(int id) {
		int ds2 = delstack;
		dotExist[delhistory[delstack++] = id] = false;
		int qq = 1;
		for (int i = 0 ; i < size; i++) {
			if (dotExist[i] && deg[i] ==1 && map[id][i]){
				deg[i] = 0; dotExist[i]=false;
				qq++;
				dotExist[delhistory[delstack++] = i] = false;
			}
		}
		for (int i = ds2;i <delstack; i++)
			deltogethernumber[i] = qq;
		calcdegree();
		history.add("D"+","+String.valueOf(id)+","+String.valueOf((new Timer()).getvalue()-timeCreated));
		matchleft --;
		System.out.println(delstack);
		if (delstack == size) {
			savehistory();
			return true;
		}
		else return false;
	}
	private boolean savehistory(){
		String fname = "S"+uname+"."+String.valueOf(level)+"."+String.valueOf(timeCreated)+"T"+String.valueOf((new Random()).nextInt(1000))+".rs";
		try {
			FileOutputStream os = owner.openFileOutput(fname,0);

			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os));
			for (int i = 0 ; i < history.size(); i++) {
				System.out.println(history.get(i));
				br.write(history.get(i)+";");
			}
			br.close();
			os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return true;
	}
	public void undelDot() {
		System.out.println("jere"+delstack);
		if (delstack==0) return;
		int qq = deltogethernumber[delstack-1];
		for (int i = 0 ; i < qq; i++)
		   dotExist[delhistory[--delstack]] = true;
		calcdegree();
		history.add("U"+","+String.valueOf((new Timer()).getvalue()-timeCreated));
		matchleft++;

	}
	
	private void calcdegree() {
		for (int i = 0; i< size; i++)
			deg[i] = 0;
		for (int i = 0 ; i < size; i++) if (dotExist[i])
			for (int j = 0 ; j < size; j++) if (dotExist[j] && map[i][j]) deg[i]++;
	}

	private int getIdByXY(int X, int Y){
		float min = 2147483412; int id =-1;
		for (int i = 0 ; i < size; i++) if (dotExist[i]){
			float dist = (X-dotX[i])*(X-dotX[i]) + (Y-dotY[i])*(Y-dotY[i]);
			if (dist < min) { id = i; min = dist;}
		}
		return id;
			
	}

	public int getIdByXY(int X, int Y, float distance2) {
		int id =  getIdByXY( X,  Y);
		if ((X-dotX[id])*(X-dotX[id]) + (Y-dotY[id])*(Y-dotY[id]) <= distance2)
			return id;
		else 
			return -1;
	}

	public float getDistanc2between(int id1, int id2){
		return (dotX[id1] - dotX[id2] )*(dotX[id1] - dotX[id2] ) + (dotY[id1] - dotY[id2] )*(dotY[id1] - dotY[id2] );
	}
}
