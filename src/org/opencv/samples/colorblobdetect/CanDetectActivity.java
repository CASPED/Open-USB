package org.opencv.samples.colorblobdetect;

import java.io.IOException;


import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
//import com.hoho.android.usbserial.examples.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import org.opencv.core.Scalar;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;


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
import android.content.SharedPreferences;



public class CanDetectActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";

    private boolean              mIsColorSelected = false;			
    private Mat                  mRgba;
  
    private ColorBlobDetector    mCanDetector;
    private Scalar				 RECTANGLE_COLOR; 
    private Scalar				 WHITE; 
    
    private CameraBridgeViewBase mOpenCvCameraView;
    
    // Para la comunicacion serial con arduino
    // Manejador de dispositivos usb 
	UsbManager manager;
	// Dispositivo en uso o {@code null}
	UsbSerialDriver sendDriver;
    
	public static final String PREFS_NAME = "colors"; 

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(CanDetectActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
   

    public CanDetectActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.color_blob_detection_surface_view);
      
        // inicia camara 
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableFpsMeter();
        mOpenCvCameraView.setMaxFrameSize(400, 400);
        //driverStatus = (TextView) findViewById(R.id.driverStatus);

        // Para la comunicacion serial
        this.manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        
        // Para guardar los valores calibrados
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        if (!((ColorsApplication)getApplication()).areAllSelected()){
	        float can_H = settings.getFloat("can_H", 0);
	        float can_S = settings.getFloat("can_S", 0);
	        float can_V = settings.getFloat("can_V", 0);
	        
	        float sea_H = settings.getFloat("sea_H", 0);
	        float sea_S = settings.getFloat("sea_S", 0);
	        float sea_V = settings.getFloat("sea_V", 0);
	        
	        float cont_H = settings.getFloat("cont_H", 0);
	        float cont_S = settings.getFloat("cont_S", 0);
	        float cont_V = settings.getFloat("cont_V", 0);
	        
	        ((ColorsApplication)getApplication()).setCanColor(new Scalar(can_H,can_S,can_V,255));
	        ((ColorsApplication)getApplication()).setSeaColor(new Scalar(sea_H,sea_S,sea_V,255));
	        ((ColorsApplication)getApplication()).setContColor(new Scalar(cont_H,cont_S,cont_V,255));
        }
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
        Log.i(TAG, "called onResume");
        // Obtener el driver para poder enviar datos al arduino 
        this.sendDriver = UsbSerialProber.acquire(this.manager);

        Log.i(TAG, "Resumed, sendDriver=" + sendDriver);
        
        // manejo del driver para escribir
        if (sendDriver == null) {
        	Log.i(TAG, "No se encontro dispositivo");
        } else {
            try {
            	sendDriver.open();
            } catch (IOException e) {
                Log.e(TAG, "Error configurando el dispositivo: " + e.getMessage(), e);
                try {
                    sendDriver.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sendDriver = null;
                return;
            }
        }
        
    }
    
    @Override
	public void onStop(){
    	super.onStop();
    	
    	/*** Guardar los colores calibrados ***/
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
    	
    	Scalar hsvCanColor=((ColorsApplication)getApplication()).getCanColor();
		Scalar hsvSeaColor=((ColorsApplication)getApplication()).getSeaColor();
	    Scalar hsvContColor=((ColorsApplication)getApplication()).getContColor();
	    
    	editor.putFloat("can_H", (float) hsvCanColor.val[0]);
        editor.putFloat("can_S", (float)hsvCanColor.val[1]);
        editor.putFloat("can_V", (float)hsvCanColor.val[2]);
         
        editor.putFloat("sea_H", (float)hsvSeaColor.val[0]);
        editor.putFloat("sea_S", (float)hsvSeaColor.val[1]);
        editor.putFloat("sea_V", (float)hsvSeaColor.val[2]);
        
        editor.putFloat("cont_H", (float)hsvContColor.val[0]);
        editor.putFloat("cont_S", (float)hsvContColor.val[1]);
        editor.putFloat("cont_V", (float)hsvContColor.val[2]);
        
        editor.commit();
    }

    @Override
	public void onDestroy() {
        super.onDestroy();
        // apaga camara 
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    
    // funcion para enviar datos a arduino por serial 
    public void sendData(char dataToSend) throws IOException {
    	    	
    	if(sendDriver != null) {
    		try{
    			// escribir bytes de datos 
    			sendDriver.setBaudRate(115200);
    			byte [] byteToSend = new byte[1]; 
    			byteToSend[0] = (byte)dataToSend;
    			sendDriver.write(byteToSend, 1000);
    		} catch (IOException e) {
    			// bla
    		} 
    	}
        
    }
    
    // funcion para recibir datos de arduino por serial 
    public char readData() throws IOException {
    	if (sendDriver != null) {
    		try {
	    		sendDriver.setBaudRate(115200);
	    		byte [] buffer = new byte[1];
	    		sendDriver.read(buffer, 1000); 
	    		return (char)buffer[0];
	    	} catch (IOException e) {
	    		// bla
	    	}
    	}
    	return '0'; 
    }

    @Override
	public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        Log.i(TAG, "Valores height "+ height + " width "+ width+ "/n");
        mCanDetector = new ColorBlobDetector();
        RECTANGLE_COLOR = new Scalar(0, 255, 255, 0);
        WHITE = new Scalar(255, 255, 255, 0);          
    }

    @Override
	public void onCameraViewStopped() {
        mRgba.release();
    }
    
    @Override
	public boolean onTouch(View v, MotionEvent event){
    	
    	// Obtener los valores globales seleccionados al calibrar
    	Scalar hsvCanColor=((ColorsApplication)getApplication()).getCanColor();
	    	
	    // Inicializar el color promedio en cada detector 
	    mCanDetector.setHsvColor(hsvCanColor);
	       
	    mIsColorSelected = true;    	
    	
    	return false;
    }


    @Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	mRgba = inputFrame.rgba();
    	  	
        if (mIsColorSelected) {
        	
        	buscarLatas();
  
        	dibujarRegiones(); 
        }

        return mRgba;
    }
    
    private void buscarLatas() {
    	mCanDetector.process(mRgba);
    	// si veo latas 
        if(mCanDetector.getNumContours()>0){     
        	Point center = mCanDetector.getNearestObject(mRgba, RECTANGLE_COLOR);
        	center.y = mCanDetector.getLowestPointSea(mRgba);
        	char pos= getPos(center);
        	System.out.print("Posicion de la lata: " + pos);       	
        	// Enviar informacion al arduino
        	try {
        		sendData(pos);
        	} catch (IOException e) {
        		// bla
        	}
        	if (pos == 'p')
        		android.os.Process.killProcess(android.os.Process.myPid());
        	
        // si no veo latas 
        } else {
        	try {
        		sendData('d');
        	} catch (IOException e) {
        		// bla
        	}
        }
    }
    
    
    public void dibujarRegiones() {
    	// **** Crear los cuadros de cada region (para debugging) ***
        // L izquierda
        Point pt1= new Point(0,0);
        Point pt2= new Point(mRgba.width()/4, mRgba.height());
        Core.rectangle(mRgba, pt1, pt2, WHITE); 
        // C centro-arriba
        Point pt3= new Point(mRgba.width()/4,0);
        Point pt4= new Point( (mRgba.width()/4)*3 , (mRgba.height()/4)*3 );
        Core.rectangle(mRgba, pt3, pt4, WHITE);
        // N centro-abajo
        Point pt5= new Point(mRgba.width()/4,(mRgba.height()/4)*3);
        Point pt6= new Point((mRgba.width()/4)*3,mRgba.height());
        Core.rectangle(mRgba, pt5, pt6, WHITE);
        // R derecha
        Point pt7= new Point((mRgba.width()/4)*3,0);
        Point pt8= new Point(mRgba.width(), mRgba.height());
        Core.rectangle(mRgba, pt7, pt8, WHITE);
    }

    /* Devuelve el char que identifica la region 
     *de la imagen en la que se encuentra el punto
     */ 
	private char getPos(Point center) {
		if(isInL(center)){ 
			return 'a';
		}else if(isInR(center)){
			return 'd';
		}else if(isInC(center)){
			return 'w';
		}else if(isInN(center)){
			return 'p';
		}
		return 0;
	}

	/* Verifica si el punto esta en la parte 
	 * central baja de la imagen
	 */
	private boolean isInN(Point center) {
		// Esquina superior izq de la region
		Point p0= new Point();
		p0.x= mRgba.width()/4;
		p0.y= (mRgba.height()/4)*3;
		
		// Esquina inferior derecha de la region
		Point p1= new Point(); 
		p1.x= (mRgba.width()/4)*3;
		p1.y= mRgba.height();
		
		return (p0.x <= center.x && center.x <= p1.x) &&
				(p0.y <= center.y && center.y <= p1.y);
	}

	/* Verifica si el punto esta en la parte 
	 * central alta de la imagen
	 */
	private boolean isInC(Point center) {
		// Esquina superior izq de la region
		Point p0= new Point();
		p0.x= mRgba.width()/4;
		p0.y= 0;
				
		// Esquina inferior derecha de la region
		Point p1= new Point(); 
		p1.x= (mRgba.width()/4)*3;
		p1.y= (mRgba.height()/4)*3;
		
		return (p0.x <= center.x && center.x <= p1.x) &&
				(p0.y <= center.y && center.y <= p1.y);
	}

	/* Verifica si el punto esta en la parte 
	 * derecha de la imagen
	 */
	private boolean isInR(Point center) {
		// Esquina superior izq de la region
		Point p0= new Point();
		p0.x= (mRgba.width()/4)*3;
		p0.y= 0;
						
		// Esquina inferior derecha de la region
		Point p1= new Point(); 
		p1.x= mRgba.width();
		p1.y= mRgba.height();
				
		return (p0.x <= center.x && center.x <= p1.x) &&
				(p0.y <= center.y && center.y <= p1.y);
	}

	/* Verifica si el punto esta en la parte 
	 * izquierda de la imagen
	 */
	private boolean isInL(Point center) {
		// Esquina superior izq de la region
		Point p0= new Point();
		p0.x= 0;
		p0.y= 0;
						
		// Esquina inferior derecha de la region
		Point p1= new Point(); 
		p1.x= mRgba.width()/4;
		p1.y= mRgba.height();
				
		return (p0.x <= center.x && center.x <= p1.x) &&
				(p0.y <= center.y && center.y <= p1.y);
	}
	
	

}
