package com.example.rbgame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.*;
public class Levelchecker {
	int maxplayed;
	Activity owner;
	public Levelchecker(Activity who){
		owner = who;
		maxplayed = read_it();
	}
	public int get_maxplayed(){
		maxplayed = read_it();
		return maxplayed;
	}
	private int read_it() {
		// TODO Auto-generated method stub
		
		try {
			FileInputStream is = owner.openFileInput("maxlevel.txt");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String s = br.readLine();
			maxplayed = Integer.parseInt(s);
			is.close();
			if (maxplayed <0) {
				maxplayed = 0;
				write_it();
			}
			return maxplayed;
			
		} catch (IOException e) {
			maxplayed = 0;
			write_it();
		}
		
		return 0;
	}
	private void write_it() {
		// TODO Auto-generated method stub
		try {
			FileOutputStream os = owner.openFileOutput("maxlevel.txt",0);

			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os));
			
				br.write(String.valueOf(maxplayed)+"\n");
			br.close();
			os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	public void levelup(){
		maxplayed++;
		if (maxplayed < 100) write_it();
		
	}
	
}
