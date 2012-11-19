package org.BENDER.eventmaker;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class PreferensesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferenses);
		
		TextView tv = (TextView)findViewById(R.id.vk_text);
				tv.setText(Html.fromHtml("Жалобы, советы, пожелания пишем  "+
	            "<a href=\"http://vk.com/ev3ntmaker\"> сюда</a> "));
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		
	}
}
