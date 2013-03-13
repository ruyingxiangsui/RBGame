package com.example.rbgame;

import android.text.format.Time;

public class Timer {
	Time t;
	public Timer(){
		t = new Time();
		
	}
	public int getvalue() {
		t.setToNow();
		int ans = t.month * 36 + t.monthDay;
		return ans* 3600*24 + t.second+t.minute*60+t.hour*3600;
	}
}
