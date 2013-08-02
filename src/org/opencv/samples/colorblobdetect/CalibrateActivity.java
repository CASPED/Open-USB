package org.opencv.samples.colorblobdetect;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.app.Activity;	
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;


public class CalibrateActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
	
		private static final String  TAG              = "OCVSample::Activity";

	    private boolean              mIsColorSelected = false;
	    private Mat                  mRgba;
	    private Scalar               mBlobColorRgba;
	    private Scalar               mBlobColorHsv;
	    private ColorBlobDetector    mDetector;
	    private Mat                  mSpectrum;
	    private Size                 SPECTRUM_SIZE;
	    private Scalar               CONTOUR_COLOR;
	    
	    private CameraBridgeViewBase mOpenCvCameraView;

	    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
	        @Override
	        public void onManagerConnected(int status) {
	            switch (status) {
	                case LoaderCallbackInterface.SUCCESS:
	                {
	                    Log.i(TAG, "OpenCV loaded successfully");
	                    mOpenCvCameraView.enableView();
	                    mOpenCvCameraView.setOnTouchListener(CalibrateActivity.this);
	                } break;
	                default:
	                {
	                    super.onManagerConnected(status);
	                } break;
	            }
	        }
	    };

	    public CalibrateActivity() {
	        Log.i(TAG, "Instantiated new " + this.getClass());
	    }
	    
	    public void onCameraViewStarted(int width, int height) {
	        mRgba = new Mat(height, width, CvType.CV_8UC4);
	        mDetector = new ColorBlobDetector();
	        mSpectrum = new Mat();
	        mBlobColorRgba = new Scalar(255);
	        mBlobColorHsv = new Scalar(255);
	        SPECTRUM_SIZE = new Size(200, 64);
	        CONTOUR_COLOR = new Scalar(255,0,0,255);	                
	    }

	    public void onCameraViewStopped() {
	        mRgba.release();
	    }
	    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
		setContentView(R.layout.activity_calibrate_can);
		// inicializo la camara 
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_calibrate_can);
        mOpenCvCameraView.setCvCameraViewListener(this);	        
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// si la camara esta abierta, cerrarla 
	    if (mOpenCvCameraView != null)
	    	mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		// si la camara esta abierta, cerrarla
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}


	public boolean onTouch(View v, MotionEvent event) {
		Log.i(TAG, "Tocado");
		// columnas y filas de la imagen
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        // toma las coordenadas del punto tocado en pantalla 
        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        // verifica que el punto tocado este dentro de la imagen 
        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        // crea un rectangulo alrededor del punto tocado
        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calcula el color promedio de la region tocada 
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");
        
        // Verificar que objeto estamos calibrando 
        Intent myIntent = getIntent();
        int flag= myIntent.getIntExtra("flag",-1); 
        
        // Colocar el color promedio en el estado global, dependiendo del caso
        if (flag == 1)
        	((ColorsApplication)getApplication()).setCanColor(mBlobColorHsv);
        if (flag == 2)
        	((ColorsApplication)getApplication()).setSeaColor(mBlobColorHsv);
        if (flag == 3)
        	((ColorsApplication)getApplication()).setContColor(mBlobColorHsv);
        
        // inicializar rango en clase detector 
        mDetector.setHsvColor(mBlobColorHsv);

        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

        // indicar que ya seleccione el color del objeto 
        mIsColorSelected = true;

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }
	
	// en cada cuadro de la imagen recibida por la camara 
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        
        // si ya seleccione el color 
        if (mIsColorSelected) {
        	// procesar la imagen 
            mDetector.process(mRgba);
            // obtener los contornos de los blobs detectados 
            List<MatOfPoint> contours = mDetector.getContours();
            
            Log.e(TAG, "Contours count: " + contours.size());
            // dibujar los contornos 
            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

            // dibuja cuadro superior con el color promedio y el rango de colores seleccionados
            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(mBlobColorRgba);

            Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
            mSpectrum.copyTo(spectrumLabel);
        }

        return mRgba;

	}

	private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
}
