package org.opencv.samples.colorblobdetect;

import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import java.math.BigInteger;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.hardware.usb.UsbManager;
import android.content.Context;



public class ColorBlobDetectionActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";

    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector    mCanDetector;
    private ColorBlobDetector    mSeeDetector;
    private ColorBlobDetector    mContDetector;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CAN_COLOR;
    private Scalar               SEE_COLOR;
    private Scalar               CONT_COLOR;
    private Scalar				 RECTANGLE_COLOR; 
    

    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
   

    public ColorBlobDetectionActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.color_blob_detection_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
    }

    @Override
    public void onPause()
    {
        super.onPause();
        // openCV
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // openCV
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    
    public void sendData() throws IOException {
    	UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
    	UsbSerialDriver sendDriver = UsbSerialProber.acquire(manager);
    	
    	if(sendDriver != null) {
    		sendDriver.open();
    		try{
    			sendDriver.setBaudRate(9600);
    			char dataToSend = '1';
    			byte [] byteToSend = new byte[1]; 
    			byteToSend[0] = (byte)dataToSend;
    			sendDriver.write(byteToSend, 1000);
    			
    			
    		} catch (IOException e) {
    			// bla
    		} finally {
    			sendDriver.close();
    		}
    	}
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mCanDetector = new ColorBlobDetector();
        mSeeDetector = new ColorBlobDetector();
        mContDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CAN_COLOR = new Scalar(255,0,0,255);
        SEE_COLOR = new Scalar(145,245,51,0);
        CONT_COLOR = new Scalar(245,245,51,0);
        RECTANGLE_COLOR = new Scalar(0, 255, 255, 0);
                
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }
    
    public boolean onTouch(View v, MotionEvent event){
    	if( ((ColorsApplication)getApplication()).areAllSelected() ) {
	    	Scalar hsvCanColor=((ColorsApplication)getApplication()).getCanColor();
	    	Scalar hsvSeeColor=((ColorsApplication)getApplication()).getSeeColor();
	    	Scalar hsvContColor=((ColorsApplication)getApplication()).getContColor();
	    	
	    	mCanDetector.setHsvColor(hsvCanColor);
	    	mSeeDetector.setHsvColor(hsvSeeColor);
	    	mContDetector.setHsvColor(hsvContColor);
	    	
	        mIsColorSelected = true;    	
    	}
    	return false;
    }


    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	
        mRgba = inputFrame.rgba();

        if (mIsColorSelected) {
            mCanDetector.process(mRgba);
            mSeeDetector.process(mRgba);
            mContDetector.process(mRgba);
            
            // contornos de mar y contenedor
            List<MatOfPoint> contoursCan = mCanDetector.getContours();
            List<MatOfPoint> contoursSee = mSeeDetector.getContours();
            List<MatOfPoint> contoursCont = mContDetector.getContours();
            
            //Log.e(TAG, "Contours count: " + contours.size());
            
            // dibuja contornos
            Imgproc.drawContours(mRgba, contoursCan, -1, CAN_COLOR);
            Imgproc.drawContours(mRgba, contoursSee, -1, SEE_COLOR);
            Imgproc.drawContours(mRgba, contoursCont, -1, CONT_COLOR);
            
            // Saca el rectangulo y punto medio de la lata con el contorno mas grande
            if(mCanDetector.getNumContours()>0){
            	// retorna el contorno mas grande 
            	MatOfPoint biggestContourCan = mCanDetector.getBiggestContour();
                
            	Rect rectangulo = Imgproc.boundingRect(biggestContourCan);           	
            	Point p1 = new Point ((double)rectangulo.x, (double)rectangulo.y); 
            	Point p2 = new Point ((double)rectangulo.x + rectangulo.width, (double)rectangulo.y + rectangulo.height);
            	Point center = new Point(rectangulo.x + (double)rectangulo.width/2, rectangulo.y + (double)rectangulo.height/2);
            	
            	Core.rectangle(mRgba, p1, p2, RECTANGLE_COLOR);
            	Core.circle(mRgba, center, 3, RECTANGLE_COLOR); 
            }
            
            /*try {
            	sendData();
            } catch (IOException e) {
            	// bla
            }*/
            //android.os.Process.killProcess(android.os.Process.myPid());
        }
        

        return mRgba;
    }

}