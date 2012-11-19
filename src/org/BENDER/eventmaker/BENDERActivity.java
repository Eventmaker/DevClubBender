package org.BENDER.eventmaker;

import org.apache.http.client.methods.HttpPost;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BENDERActivity extends Activity {

	Dialog dialog;
	SharedPreferences sPref;
	HttpPost httppost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		httppost = new HttpPost("https://my.devclub.com.ua/index/loginHandler/");
		sPref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

//		if (!sPref.getString("HasData", "").contains("yes")) {
//			showLoginDialog();
//		} else if (sPref.getString("Login", "").length() > 1) {
//			CheckLogin loginIn = new CheckLogin(BENDERActivity.this, httppost,
//					sPref.getString("Login", "").toString(), sPref.getString(
//							"Password", "").toString(), sPref, dialog);
//			loginIn.check();
//		}
	}

	void showMyToast(String msg) {
		LayoutInflater LInflater = getLayoutInflater();
		View layout = LInflater.inflate(R.layout.my_toast,
				(ViewGroup) findViewById(R.id.myToast));
		TextView title = (TextView) layout.findViewById(R.id.mytoasttext);
		title.setText(msg);
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(1000);
		toast.setView(layout);
		toast.show();
	}

	void showLoginDialog() {

		dialog = new Dialog(this, R.style.dialogLoad);
		dialog.setContentView(R.layout.login);
		dialog.findViewById(R.id.btn_login).setOnClickListener(
				new Button.OnClickListener() {
					public void onClick(View v) {

						EditText login = (EditText) dialog
								.findViewById(R.id.txt_username);
						EditText pass = (EditText) dialog
								.findViewById(R.id.txt_password);

						Editor ed = sPref.edit();
						ed.putString("Login", login.getText().toString());
						ed.putString("Password", pass.getText().toString());
						ed.putString("HasData", "yes");
						ed.commit();
						CheckLogin loginIn = new CheckLogin(
								BENDERActivity.this, httppost, login.getText()
										.toString(), pass.getText().toString(),
								sPref, dialog);
						loginIn.check();
					}
				});
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				return;
			}
		});
		dialog.show();
	}

	public void onTaskList(View v) {
		if (isOnline()) {
			if (sPref.getString("HasData", "").contains("yes")) {
				Intent i = new Intent(this, TaskList.class);
				startActivity(i);
			} else {
				showLoginDialog();
			}
		} else {
			showMyToast("No Internet Connection");
		}
	}

	public void onComplexity(View v) {
		if (isOnline()) {
			if (sPref.getString("HasData", "").contains("yes")) {
				Intent i = new Intent(this, Complexity.class);
				startActivity(i);
			} else if (sPref.getString("Login", "").length() > 1) {
				CheckLogin loginIn = new CheckLogin(BENDERActivity.this,
						httppost, sPref.getString("Login", "").toString(),
						sPref.getString("Password", "").toString(), sPref,
						dialog);
				loginIn.check();
			} else {
				showLoginDialog();
			}
		} else {
			showMyToast("No Internet Connection");
		}
	}

	public void onDocs(View v) {
		Intent i = new Intent(this, DocsActivity.class);
		startActivity(i);
	}

	public void onPreferenses(View v) {
		Intent i = new Intent(this, PreferensesActivity.class);
		startActivity(i);
	}

	public void onHome(View v) {
		return;
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		if (nInfo != null && nInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}
}
