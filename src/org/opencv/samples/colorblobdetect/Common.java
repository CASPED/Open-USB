package org.opencv.samples.colorblobdetect;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Point;

public class Common {
	
	private static Mat mIntermediateMat;
	private static Size mSize0;

	public static Mat filterImage(CvCameraViewFrame inputFrame) {
        Mat mRgba = inputFrame.rgba();
        
        if (mIntermediateMat == null) mIntermediateMat = new Mat();
        if (mSize0 == null) mSize0 = new Size();
        
    	Size mSizeRgba = mRgba.size();
    	Mat mRgbaWindow = mRgba.submat(0, (int) mSizeRgba.height, 0, (int) mSizeRgba.width);
    	Size mSizeRgbaInner = mRgbaWindow.size();
	    
	    mRgbaWindow.copyTo(mIntermediateMat);
	    //Filtros de Oliver:
	    ////Imgproc.resize(mIntermediateMat, mIntermediateMat, mSize0, 0.2, 0.2, Imgproc.INTER_NEAREST);
	    ////Imgproc.GaussianBlur(mIntermediateMat, mIntermediateMat, new Size(9, 9), 10.0);
	    Imgproc.cvtColor(mIntermediateMat, mIntermediateMat, Imgproc.COLOR_RGBA2RGB);
	    Imgproc.pyrMeanShiftFiltering(mIntermediateMat, mIntermediateMat, 10, 200);
	    Imgproc.cvtColor(mIntermediateMat, mIntermediateMat, Imgproc.COLOR_RGB2RGBA);
	    ////Imgproc.resize(mIntermediateMat, mIntermediateMat, mSizeRgbaInner, 0., 0., Imgproc.INTER_NEAREST);
	    ////Core.convertScaleAbs(mIntermediateMat, mIntermediateMat, 1./128, 0);
	    ////Core.convertScaleAbs(mIntermediateMat, mIntermediateMat, 128, 0);
	    
	    //Filtros de Jennifer:
	    
	    /*Imgproc.GaussianBlur(mIntermediateMat, mIntermediateMat, new Size(11,11), 0);
	    Imgproc.cvtColor(mIntermediateMat, mIntermediateMat, Imgproc.COLOR_RGBA2RGB);
	    Imgproc.pyrMeanShiftFiltering(mIntermediateMat, mIntermediateMat, 10, 200);
	    Imgproc.cvtColor(mIntermediateMat, mIntermediateMat, Imgproc.COLOR_RGB2RGBA);*/
	    
	    mIntermediateMat.copyTo(mRgbaWindow);
	    
	    return mRgba;
	}
	
	public static CameraBridgeViewBase getCamera(CvCameraViewListener2 activity, int camera_id) {
		CameraBridgeViewBase mOpenCvCameraView = (CameraBridgeViewBase) ((Activity)activity).findViewById(camera_id);
        mOpenCvCameraView.setCvCameraViewListener(activity);
        mOpenCvCameraView.enableFpsMeter();
        mOpenCvCameraView.setMaxFrameSize(400, 400);
        return mOpenCvCameraView;
	}

}
