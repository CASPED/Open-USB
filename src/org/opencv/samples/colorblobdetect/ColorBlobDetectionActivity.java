package org.opencv.samples.colorblobdetect;

import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import android.view.View.OnTouchListener;
import android.hardware.usb.UsbManager;
import android.hardware.SensorManager;
import android.content.Context;
import android.content.SharedPreferences;




public class ColorBlobDetectionActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";

    private boolean              mIsColorSelected = false;
    private boolean              modoContenedor = false; 			
    private Mat                  mRgba;
  
    private ColorBlobDetector    mCanDetector;
    private ColorBlobDetector    mSeaDetector;
    private ColorBlobDetector    mContDetector;
    private Scalar               CAN_COLOR;
    private Scalar               SEA_COLOR;
    private Scalar               CONT_COLOR;
    private Scalar				 RECTANGLE_COLOR; 
    private Scalar				 WHITE;
	private Mat                  mIntermediateMat;
    
    private char 				 prevMsg = '9';
    private boolean 			 esperandoReinicio = false; 
    private boolean				 evitandoObstaculos = false; 
    private double 				 lowestSea = -1; 
    private double 				 highestSea; 
    private int 				 segsDelay = 1; 
    private int					 frameSteps = 5*segsDelay; 
    private int					 frameCount = 0; 
    
    private CameraBridgeViewBase mOpenCvCameraView;
    
    //para usar el compas del celular 
    //private SensorManager mSensorManager;
    
    // *** Para la comunicacion serial con arduino *** //
    // Manejador de dispositivos usb 
	private UsbManager manager;
	// Dispositivo en uso o {@code null}
	private UsbSerialDriver driver; 
    
	public static final String PREFS_NAME = "colors"; 

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
        mOpenCvCameraView = Common.getCamera(this, R.id.color_blob_detection_activity_surface_view);
        
        //driverStatus = (TextView) findViewById(R.id.driverStatus);

        // Para la comunicacion serial
        this.manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        Log.i(TAG, "Despues de pedir driver");
        
        //Para la orientacion 
        //this.mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        
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
        
        // Cierra driver
        if (driver != null) {
            try {
                driver.close();
            } catch (IOException e) {
                // Ignore.
            }
            driver = null;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
        Log.i(TAG, "called onResume");
        // Obtener el driver para poder enviar datos al arduino 
        this.driver = UsbSerialProber.acquire(this.manager);

        Log.i(TAG, "Resumed, driver=" + driver);
        
        // manejo del driver para escribir
        if (driver == null) {
        	Log.i(TAG, "No se encontro dispositivo");
        } else {
            try {
            	driver.open();
            } catch (IOException e) {
                Log.e(TAG, "Error configurando el dispositivo: " + e.getMessage(), e);
                try {
                    driver.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                driver = null;
                return;
            }
        }
        
    }
    
    @Override
	public void onStop(){
    	super.onStop();
    	
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
    	    	
    	if(driver != null) {
    		try{
    			// escribir bytes de datos 
    			driver.setBaudRate(115200);

    			byte [] byteToSend = new byte[1]; 
    			byteToSend[0] = (byte)dataToSend;
    			driver.write(byteToSend, 1000);
    		} catch (IOException e) {
    			// bla
    		} 
    	}
        
    }
    
    // funcion para recibir datos de arduino por serial 
    public char readData() throws IOException {
    	if (driver != null) {
    		try {
	    		driver.setBaudRate(115200);
	    		byte [] buffer = new byte[1];
	    		driver.read(buffer, 1000); 
	    		return (char)buffer[0];
	    	} catch (IOException e) {
	    		// bla
	    	}
    	}
    	return '9'; 
    }

    @Override
	public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        Log.i(TAG, "Valores height "+ height + " width "+ width+ "/n");
        mCanDetector = new ColorBlobDetector();
        mSeaDetector = new ColorBlobDetector();
        mContDetector = new ColorBlobDetector();
        CAN_COLOR = new Scalar(255,0,0,255);
        SEA_COLOR = new Scalar(145,245,51,0);
        CONT_COLOR = new Scalar(245,245,51,0);
        RECTANGLE_COLOR = new Scalar(0, 255, 255, 0);
        WHITE = new Scalar(255, 255, 255, 0);
    }

    @Override
	public void onCameraViewStopped() {
        mRgba.release();
        if (mIntermediateMat != null)
            mIntermediateMat.release();
        mIntermediateMat = null;
    }
    
    @Override
	public boolean onTouch(View v, MotionEvent event){
    	
    	// obtener los valores globales seleccionados al calibrar
    	Scalar hsvCanColor=((ColorsApplication)getApplication()).getCanColor();
		Scalar hsvSeaColor=((ColorsApplication)getApplication()).getSeaColor();
	    Scalar hsvContColor=((ColorsApplication)getApplication()).getContColor();
	    	
	    //inicializar el color promedio en cada detector 
	    mCanDetector.setHsvColor(hsvCanColor);
	    mSeaDetector.setHsvColor(hsvSeaColor);
	    mContDetector.setHsvColor(hsvContColor);
	    
	    mIsColorSelected = true;
	    
	    initArduino();
	    //esperarReinicio(); 
	        	
    	return false;
    }


    @Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	mRgba = Common.filterImage(inputFrame);
    	
    	// saltarme los frames sin procesar si estoy esperando reinicio
    	if (esperandoReinicio) {
    		try {
	    		if (readData() == 'r') {
	    			esperandoReinicio = false;
	    		}
	    	} catch (IOException e) {
	    		// bla
	    	}
    		return mRgba; 
    	}
    	
    	// leer de arduino para ver si esta evitando obstaculos y quedarme callado 
    	try {
    		if (readData() == 'h') {
    			esperandoReinicio = true;
    			return mRgba; 
    		}
    	} catch (IOException e) {
    		// bla
    	}

    	
    	// aqui si proceso
        if (mIsColorSelected) {
        	
            highestSea = 0; 
        	
        	if (evitarMar()) {
        		return mRgba; 
        	}
        		
        	if (modoContenedor) {
        		buscarContenedor();
        	} else {
        		buscarLatas();
        	}
                  
        	dibujarRegiones(); 
                  
            //android.os.Process.killProcess(android.os.Process.myPid());
        }

        return mRgba;
    }
   
    private int buscarLatas() {
    	mCanDetector.process(mRgba);
    	// si tengo blobs negros
        if(mCanDetector.getNumContours()>0){  
        	// le paso el punto alto del mar para no detectar latas por encima 
        	// esta condicion esta comentada ahora
        	Blob can = mCanDetector.getNearestObject(mRgba, RECTANGLE_COLOR, highestSea);
        	Point center = can.center;
        	
        	// si veo latas 
        	if (center.y != -1) {
        		
        		center.y = mCanDetector.getLowestPointSea(mRgba);
            	char pos= getPos(center, modoContenedor);
            	
            	// si esta abajo pero es muy peq para ser lata (caso sombras)
            	// mando a mover hacia adelante y me salgo de la funcion 
            	if (pos == 'p' && can.area < 5000 ) {
    	        	// Enviar informacion al arduino
            		if (prevMsg != 'w') {
	    	        	try {
	    	        		Log.i(TAG, "Send w"); 
	    	        		sendData('w');
	    	        	} catch (IOException e) {
	    	        		// bla
	    	        	}
	    	        	prevMsg = 'w';
            		}
            		return 1; 
    	        	
            	}
            	
            	
            	// si trate de agarrar antes y no pude, alejarme
            	// si ocurre dos veces, acercarme 
            	//if (prevMsg == '0' && pos == '0') {
            	/*if (prevMsg == 'p' && pos == 'p') {
            		try {
            			timesP++; 
            			if (timesP == 2){
            				sendData('w');
            				for(int i=0; i<1000; i++);
            			} else if (timesP == 1) {
            				sendData('a'); 
            				for(int i=0; i<1000; i++);
            			} else if (timesP > 2) {
            				timesP = 0;
            	        	try {
            	        		sendData('d');
            	        	} catch (IOException e) {
            	        		// bla
            	        	} 
            	        	for(int i=0; i<1000; i++); 
            			}
    	        	} catch (IOException e) {
    	        		// bla
    	        	}
    	        	prevMsg = pos; 
    	        	
            	}*/ 
            	
            	if (prevMsg != pos) {
            		Log.i(TAG, "SEND " + pos);
    	        	// Enviar informacion al arduino
    	        	try {
    	        		sendData(pos);
    	        	} catch (IOException e) {
    	        		// bla
    	        	}
    	        	prevMsg = pos; 
    	        	
            	} 
            	
            	// si me pare a agarrar, debo esperar que el robot termine el movimiento 
            	if(pos == 'p'){
    				//esperarReinicio();
            		esperandoReinicio = true; 
            		return 1; 
    			}
        	}
        // no veo latas en el area o blobs negros
        } else {
        	// giro hacia la izquierda
        	if (prevMsg != 'w') {
        		Log.i(TAG, "No veo latas!");
        		Log.i(TAG, "SEND w");
	        	try {
	        		sendData('w');
	        	} catch (IOException e) {
	        		// bla
	        	}
	        	prevMsg = 'w'; 
        	}        	        	
        }     
        return 1; 
    }
    
    private void buscarContenedor() {
    	mContDetector.process(mRgba);
		
		if(mContDetector.getNumContours()>0){
			Blob contenedor = mContDetector.getNearestObject(mRgba, CONT_COLOR, lowestSea);
			Point center = contenedor.center;
        	center.y = mContDetector.getLowestPointSea(mRgba);
        	char pos= getPos(center, modoContenedor);
        	
        	if (prevMsg != pos) {
        	//System.out.print("Posicion del contenedor: " + pos);
        		Log.i(TAG, "SEND " + pos);
        	// Enviar informacion al arduino
        		try {
        			sendData(pos);
        		} catch (IOException e) {
        			// bla
        		}
        		prevMsg = pos;
        	}
			
        	if(pos == 'c'){
        		modoContenedor = false;
        		esperandoReinicio=true;
        		
        	}
        		
        }else{
        	// seguir hacia adelante si no veo el contenedor 
        	// dejar de evitacion de obs y mar se encargue de cambiar dir 
        	if (prevMsg != 'w') {
        		Log.i(TAG, "SEND w");
        		try {
            		Log.i(TAG, "No veo al contenedor!");
        			sendData('w');
        		} catch (IOException e) {
        			// bla
        		}
        	}
        }
				
    }
    
    
    private void initArduino(){
    	try {
    		sendData('i');
    	} catch (IOException e) {
    		// bla
    	}
    }
    
    private void esperarReinicio(){
    	boolean esperando = true;
    	while (esperando){
	    	try {
	    		if (readData() == 'r') {
	    			esperando = false;
	    		}
	    	} catch (IOException e) {
	    		// bla
	    	}
    	}
    }
    
    private boolean evitarMar() {
        mSeaDetector.process(mRgba);       
        
        if(mSeaDetector.getNumContours()>0){
        	
        	mSeaDetector.drawRectangles(mRgba, SEA_COLOR);
        	
            //obtener el punto mas bajo y mas alto del mar
        	lowestSea = mSeaDetector.getLowestPointSea(mRgba);
        	highestSea =  mSeaDetector.getHighestPointSea(mRgba);
        	
        	// si tengo al mar cerca, giro a la derecha 
        	if(lowestSea > (mRgba.height()/8)*7){
        		if (prevMsg != 'd') {
	        		try {
	                	Log.i(TAG, "SEND d");
	            		sendData('d');
	            	} catch (IOException e) {
	            		// bla
	            	}
	        		prevMsg = 'd'; 
        		}
        		return true;
        	}
        }
        
        return false;
    }
    
    public void dibujarRegiones() {
    	// **** Crear los cuadros de cada region (para debugging) ***
        // L izquierda
        Point pt1= new Point(0,0);
        Point pt2= new Point(mRgba.width()/4, mRgba.height());
        Core.rectangle(mRgba, pt1, pt2, WHITE); 
        // C centro-arriba
        Point pt3= new Point(mRgba.width()/4,0);
        Point pt4= new Point( (mRgba.width()/4)*3 , (mRgba.height()/4)*3.2 );
        Core.rectangle(mRgba, pt3, pt4, WHITE);
        // N centro-abajo
        Point pt5= new Point(mRgba.width()/4,(mRgba.height()/4)*3.2);
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
	private char getPos(Point center, boolean modoContenedor) {
		if(isInL(center)){ 
			return 'a';
		}else if(isInR(center)){
			return 'd';
		}else if(isInC(center)){
			return 'w';
		}else if(isInN(center)){
			if (modoContenedor){
				return 'c'; 
			} else {
				return 'p';
				//return '0';
			}
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
		p0.y= (mRgba.height()/4)*3.2;
		
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
		p1.y= (mRgba.height()/4)*3.2;
		
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
