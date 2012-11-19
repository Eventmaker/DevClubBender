package org.BENDER.eventmaker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MinGWCommands extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mingw_commands);
	}

	public void onHome(View v) {
		finish();
	}
}
