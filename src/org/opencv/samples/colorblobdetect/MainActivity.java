package org.opencv.samples.colorblobdetect;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button calibrar = (Button) findViewById(R.id.buttonCalibrar);
        calibrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), CalibrateCanActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
        
    	Button detectar = (Button) findViewById(R.id.buttonDetectar);
        detectar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ColorBlobDetectionActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
	}


}
