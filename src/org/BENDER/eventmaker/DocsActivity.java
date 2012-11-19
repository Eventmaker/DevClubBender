package org.BENDER.eventmaker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DocsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.docs);
	}

	public void onHome(View v) {
		finish();
	}

	public void onCodstyleClick(View v) {
		Intent i = new Intent(this, Codstyle.class);
		startActivity(i);
	}

	public void onMinClick(View v) {
		Intent i = new Intent(this, MinGWCommands.class);
		startActivity(i);
	}

	public void onThemesClick(View v) {
		Intent i = new Intent(this, WeekThemes.class);
		startActivity(i);
	}
}
