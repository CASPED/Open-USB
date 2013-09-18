package org.opencv.samples.colorblobdetect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class ColorBlobDetector {
    private static final String  TAG              = "OCVSample::Activity";

    // Cotas inferior y superior para chequear el rango HSV del color en el espacio 
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // porcentaje de area minimo para filtrar contornos encontrados
    private static double mMinContourArea = 0.1;
    // Radio de color para chequear rango HSV del color en el espacio 
    private Scalar mColorRadius = new Scalar(25,50,50,0);

    private Mat mSpectrum = new Mat();
    // Listas de objetos detectados 
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
    private List<Double> mSizes = new ArrayList<Double>(); 

    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();

    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }

    // inicializa el rango de colores 
    public void setHsvColor(Scalar hsvColor) {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;

        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }
    
    // funcion no utilizada, inicializa el rango de color negro manualmente "hardcoded"
    public void setHsvBlack(){
    	mLowerBound.val[0] = 0;
    	mUpperBound.val[0] = 255;
    	
    	mLowerBound.val[1] = 0;
    	mUpperBound.val[1] = 247;
    	
    	mLowerBound.val[2] = 0;
    	mUpperBound.val[2] = 49;
    	
    	mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;
        
        Mat spectrumHsv = new Mat(1, 255-0, CvType.CV_8UC3);

        for (int j = 0; j < 255-0; j++) {
            byte[] tmp = {(byte)(0+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);

    }

    public Mat getSpectrum() {
        return mSpectrum;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }

 
    public void process(Mat rgbaImage) {
    	// baja dos veces la calidad de la imagen 
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

        // pasa de rgb a hsv
        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        // Crea una mascara con los blobs en el rango y dilata la imagen 
        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        // lista donde coloca los contornos de blobs encontrados
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Encuentra el area maxima del contorno para hacer resize posterior 
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filtra los contornos encontrados por area y reacomoda para que encaje en la imagen original 
        mSizes.clear();
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            Double area =  Imgproc.contourArea(contour); 
            if (area > mMinContourArea*maxArea) {
                Core.multiply(contour, new Scalar(4,4), contour);
                // mantiene una lista con el area de los contornos 
                mSizes.add(area); 
                // mantiene una lista de contornos 
                mContours.add(contour);
            }
        }
    }
    
    // obtiene el contorno de mayor area 
    public MatOfPoint getBiggestContour() {
    	Iterator<Double> each = mSizes.iterator();
    	int index = 0; 
    	int max_index = 0; 
    	Double max_size = 0.0; 
    	Double current_size; 
    	
		Log.e(TAG, "tam_sizes: " + mSizes.size()); 
		Log.e(TAG, "tam_contours: " + mContours.size()); 

    	while (each.hasNext()){
    		current_size = each.next();
    		if (current_size > max_size) {
    			Log.e(TAG, "max_index: " + max_index); 
    			max_size = current_size; 
    			max_index = index; 
    		}
    		index++; 
    	}
    	return mContours.get(max_index); 
    }
    
    /*
     * Devuelve el punto central de la lata cuya posicion sea
     * la mas baja en la imagen 
     */
    public Point getNearestObject(Mat mRgba, Scalar COLOR){
    	Iterator<MatOfPoint> contours = mContours.iterator();
    	double max_y= -1;
    	double x=0;

    	while (contours.hasNext()){
    		MatOfPoint contour= contours.next();
    		
    		// Sacar el rectangulo y su centro
    		Rect rectangle = Imgproc.boundingRect(contour);           	
        	double x_center = rectangle.x + rectangle.width/2;
        	double y_center = rectangle.y + rectangle.height/2;
        	
        	// Quedarse con el mas bajo en la imagen
        	if(y_center > max_y){
        		max_y= y_center;
        		x= x_center;
        	}
        	// Solo para debugging
        	Point p1 = new Point (rectangle.x,rectangle.y); 
        	Point p2 = new Point (rectangle.x+rectangle.width, rectangle.y + rectangle.height);
        	Core.rectangle(mRgba,p1,p2, COLOR);
    	}
    	Point lowerCenter = new Point(x , max_y);
    	Core.circle(mRgba, lowerCenter, 3, COLOR);
    	return lowerCenter;
    }
    
    public double getLowestPointSea(Mat mRgba){
    	Iterator<MatOfPoint> contours = mContours.iterator();
    	double max_y= -1;
    	double x= 0;

    	while (contours.hasNext()){
    		MatOfPoint contour= contours.next();
    		
    		// Sacar el rectangulo y el punto mas bajo
    		Rect rectangle = Imgproc.boundingRect(contour);           	
        	double y_r = rectangle.y + rectangle.height;
        	double x_r= rectangle.x + rectangle.width/2;
        			
        	// Quedarse con el mas bajo en la imagen
        	if(y_r > max_y){
        		max_y= y_r;
        		x= x_r;
        	}
    	}
    	
    	Core.circle(mRgba, new Point(x,max_y), 3, new Scalar(255,0,0,255));	
    	return max_y;
    }
    
    public void drawRectangles(Mat mRgba, Scalar COLOR) {
    	Iterator<MatOfPoint> contours = mContours.iterator();
    	
    	 while (contours.hasNext()){
     		MatOfPoint contour= contours.next();
         	Rect rectangle = Imgproc.boundingRect(contour); 
         	Point p1 = new Point (rectangle.x,rectangle.y); 
         	Point p2 = new Point (rectangle.x+rectangle.width, rectangle.y + rectangle.height);
         	Core.rectangle(mRgba,p1,p2,COLOR);
         }
     	
    }
    
    // da el numero de contornos encontrados 
    public int getNumContours() {
    	return mContours.size();
    }

    // devuelve todos los contornos
    public List<MatOfPoint> getContours() {
        return mContours;
    }
}
