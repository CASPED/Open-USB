package org.opencv.samples.colorblobdetect;

import org.opencv.core.Scalar;

import android.app.Application;

public class ColorsApplication extends Application {
	
	private Scalar avgCanColor;
	private Scalar avgSeeColor;
	private Scalar avgContColor;
	private boolean canColorSelected = false;
	private boolean seeColorSelected = false;
	private boolean contColorSelected = false;
	
	public Scalar getSeeColor() {
		return avgSeeColor;
	}
	
	public void setSeeColor(Scalar hsvColor) {
		this.avgSeeColor = hsvColor;
		this.seeColorSelected = true;
		
	}
	
	public Scalar getCanColor() {
		return avgCanColor;
	}
	
	public void setCanColor(Scalar hsvColor) {
		this.avgCanColor = hsvColor; 
		this.canColorSelected = true;
	}
	
	public Scalar getContColor() {
		return avgContColor;
	}
	
	public void setContColor(Scalar hsvColor) {
		this.avgContColor = hsvColor; 
		this.contColorSelected = true;
	}
	
	public boolean areAllSelected(){
		return seeColorSelected && canColorSelected && contColorSelected;
		
	}
	

}
