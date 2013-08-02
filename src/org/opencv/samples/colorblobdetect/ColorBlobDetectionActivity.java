package org.opencv.samples.colorblobdetect;

import java.util.List;
import java.io.IOException;


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
  
    private ColorBlobDetector    mCanDetector;
    private ColorBlobDetector    mSeaDetector;
    private ColorBlobDetector    mContDetector;
    private Scalar               CAN_COLOR;
    private Scalar               SEA_COLOR;
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
        // inicia camara 
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
    }

    @Override
    public void onPause()
    {
        super.onPause();
        // apaga camara 
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        // apaga camara 
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    
    // funcion para enviar datos a arduino por serial 
    public void sendData() throws IOException {
    	// obtiene manejador de dispositivos usb 
    	UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
    	// encuentra el primer driver disponible 
    	UsbSerialDriver sendDriver = UsbSerialProber.acquire(manager);
    	
    	// si encontre driver
    	if(sendDriver != null) {
    		sendDriver.open();
    		try{
    			// escribir bytes de datos 
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
        mSeaDetector = new ColorBlobDetector();
        mContDetector = new ColorBlobDetector();
        CAN_COLOR = new Scalar(255,0,0,255);
        SEA_COLOR = new Scalar(145,245,51,0);
        CONT_COLOR = new Scalar(245,245,51,0);
        RECTANGLE_COLOR = new Scalar(0, 255, 255, 0);
                
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }
    
    public boolean onTouch(View v, MotionEvent event){
    	// ejecutar solo si ya calibre todo
    	if( ((ColorsApplication)getApplication()).areAllSelected() ) {
    		// obtener los valores globales seleccionados al calibrar
	    	Scalar hsvCanColor=((ColorsApplication)getApplication()).getCanColor();
	    	Scalar hsvSeaColor=((ColorsApplication)getApplication()).getSeaColor();
	    	Scalar hsvContColor=((ColorsApplication)getApplication()).getContColor();
	    	
	    	//inicializar el color promedio en cada detector 
	    	mCanDetector.setHsvColor(hsvCanColor);
	    	mSeaDetector.setHsvColor(hsvSeaColor);
	    	mContDetector.setHsvColor(hsvContColor);
	    	
	        mIsColorSelected = true;    	
    	}
    	return false;
    }


    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	
        mRgba = inputFrame.rgba();

        if (mIsColorSelected) {
        	// un detector para cada objeto 
            mCanDetector.process(mRgba);
            mSeaDetector.process(mRgba);
            mContDetector.process(mRgba);
            
            // una lista de contornos para cada objeto 
            List<MatOfPoint> contoursCan = mCanDetector.getContours();
            List<MatOfPoint> contoursSea = mSeaDetector.getContours();
            List<MatOfPoint> contoursCont = mContDetector.getContours();
            
            //Log.e(TAG, "Contours count: " + contours.size());
            
            // dibuja contornos
            Imgproc.drawContours(mRgba, contoursCan, -1, CAN_COLOR);
            Imgproc.drawContours(mRgba, contoursSea, -1, SEA_COLOR);
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
            
            // aqui va el codigo donde envio datos a la arduino 
            
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