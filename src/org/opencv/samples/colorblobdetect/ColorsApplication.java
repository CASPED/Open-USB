package org.opencv.samples.colorblobdetect;

import org.opencv.core.Scalar;

import android.app.Application;

public class ColorsApplication extends Application {
	
	// color promedio de los blobs segun el objeto
	private Scalar avgCanColor;
	private Scalar avgSeaColor;
	private Scalar avgContColor;
	// booleanos para saber si fue seleccionado el color
	private boolean canColorSelected = false;
	private boolean seaColorSelected = false;
	private boolean contColorSelected = false;
	
	public Scalar getSeaColor() {
		return avgSeaColor;
	}
	
	public void setSeaColor(Scalar hsvColor) {
		this.avgSeaColor = hsvColor;
		this.seaColorSelected = true;
		
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
	
	// si todos los colores ya fueron calibrados 
	public boolean areAllSelected(){
		return seaColorSelected && canColorSelected && contColorSelected;
		
	}
	

}
