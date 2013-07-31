package org.opencv.samples.colorblobdetect;

import org.opencv.core.Scalar;

import android.app.Application;

public class ColorsApplication extends Application {
	
	private Scalar avgCanColor;
	
	public Scalar getCanColor() {
		return avgCanColor;
	}
	
	public void setCanColor(Scalar hsvColor) {
		this.avgCanColor = hsvColor; 
	}
	

}
