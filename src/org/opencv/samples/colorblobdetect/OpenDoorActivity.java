package org.opencv.samples.colorblobdetect;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class OpenDoorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open_door);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.open_door, menu);
		return true;
	}

}
