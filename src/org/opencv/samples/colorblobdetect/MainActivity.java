package org.opencv.samples.colorblobdetect;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.http.debug.HTTPrint;


public class MainActivity extends Activity {
	
	HTTPrint http_print;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/** Aquí se inicializa el debugger de http, en el parametro hay que poner el IP del
		 * servidor.
		 */
		http_print = new HTTPrint("http://192.168.0.105");
		//Cada print debería enviarse al servidor:
		System.out.print("Planificador incializado.");
		
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
        
        
        
        
	}


}
