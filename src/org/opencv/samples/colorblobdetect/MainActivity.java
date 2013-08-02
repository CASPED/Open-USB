package org.opencv.samples.colorblobdetect;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// boton acceso a calibrar lata
		Button calibrarLata = (Button) findViewById(R.id.buttonCalibrarLatas);
        calibrarLata.setOnClickListener(new View.OnClickListener() {
        	// si hago click, redireccionar a la actividad
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
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ColorBlobDetectionActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
        
        
        
        
	}


}
