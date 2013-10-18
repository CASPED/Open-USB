package org.opencv.samples.colorblobdetect;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;


public class MainActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// boton acceso a calibrar lata
		Button calibrarLata = (Button) findViewById(R.id.buttonCalibrarLatas);
        calibrarLata.setOnClickListener(new View.OnClickListener() {
        	// si hago click, redireccionar a la actividad
            @Override
			public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), CalibrateActivity.class);
                // flag indica variable global de color a guardar
                myIntent.putExtra("flag", 1);
                startActivityForResult(myIntent, 0);
            }

        });
        
		// boton acceso a calibrar mar
        Button calibrarMar = (Button) findViewById(R.id.buttonCalibrarMar);
        calibrarMar.setOnClickListener(new View.OnClickListener() {
        	// si hago click, redireccionar a la actividad
            @Override
			public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), CalibrateActivity.class);
                // flag indica variable global de color a guardar
                myIntent.putExtra("flag", 2);
                startActivityForResult(myIntent, 0);
            }

        });
        
		// boton acceso a calibrar contenedor
        Button calibrarCont = (Button) findViewById(R.id.buttonCalibrarCont);
        calibrarCont.setOnClickListener(new View.OnClickListener() {
        	// si hago click, redireccionar a la actividad
            @Override
			public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), CalibrateActivity.class);
                // flag indica variable global de color a guardar
                myIntent.putExtra("flag", 3);
                startActivityForResult(myIntent, 0);
            }

        });
        
		// boton acceso a la deteccion 
    	Button detectar = (Button) findViewById(R.id.buttonDetectar);
        detectar.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ColorBlobDetectionActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
        
     // Prueba de demostracion. Detectar Lata 
    	Button detectarLata = (Button) findViewById(R.id.buttonCanDetect);
        detectarLata.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), CanDetectActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
        
     // Prueba de demostracion. Detectar Contenedor
    	Button detectarCont = (Button) findViewById(R.id.buttonContDetect);
        detectarCont.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ContDetectActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
        
     // Prueba de agarrar lata
        Button agarrarLata = (Button) findViewById(R.id.buttonAgarrarLata);
        agarrarLata.setOnClickListener(new View.OnClickListener() {
        	private static final String TAG = "OCVSample::Activity"; 
        	UsbManager manager;
        	UsbSerialDriver sendDriver; 
        	
        	@Override
        	public void onClick(View view) {
        		Log.i(TAG, "Send p");
        		this.manager = (UsbManager) getSystemService(Context.USB_SERVICE);
            	this.sendDriver = UsbSerialProber.acquire(this.manager);
            	
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
            	
            	if(sendDriver != null) {
            		try{
            			// escribir bytes de datos 
            			sendDriver.setBaudRate(115200);
            			byte [] byteToSend = new byte[1]; 
            			byteToSend[0] = (byte)'p';
            			sendDriver.write(byteToSend, 1000);
            			Log.e(TAG, "Si pude enviar");
            		} catch (IOException e) {
            			Log.e(TAG, "No pude enviar");
            		} finally {
            			try {
                            sendDriver.close();
                        } catch (IOException e2) {
                        	Log.i(TAG, "Se ha cerrado el driver");
                        }
            		}
            	}
        	}
        }); 
        
        
     // Prueba de demostracion. Abrir Contenedor
    	Button abrirCont = (Button) findViewById(R.id.buttonOpenDoor);
        abrirCont.setOnClickListener(new View.OnClickListener() {
        	private static final String  TAG = "OCVSample::Activity";
        	UsbManager manager;
        	UsbSerialDriver sendDriver;
        	
            @Override
			public void onClick(View view) {
        		Log.i(TAG, "Send 2");

            	this.manager = (UsbManager) getSystemService(Context.USB_SERVICE);
            	this.sendDriver = UsbSerialProber.acquire(this.manager);
            	
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
            	
            	if(sendDriver != null) {
            		try{
            			Log.i(TAG, "Send 2");
            			// escribir bytes de datos 
            			sendDriver.setBaudRate(115200);
            			byte [] byteToSend = new byte[1]; 
            			byteToSend[0] = (byte)'2';
            			sendDriver.write(byteToSend, 1000);
            			Log.e(TAG, "Si pude enviar");
            		} catch (IOException e) {
            			Log.e(TAG, "No pude enviar");
            		} finally {
            			try {
                            sendDriver.close();
                        } catch (IOException e2) {
                        	Log.i(TAG, "Se ha cerrado el driver");
                        }
            		}
            	}
            	
            }

        });
        
	}


}
