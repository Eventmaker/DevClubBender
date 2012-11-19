package org.BENDER.eventmaker;

import org.apache.http.client.methods.HttpPost;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;

public class CheckLogin implements LoginListener {

	private Activity context;
	private HttpPost contentUrl;
	private String login;
	private String pass;
	private SharedPreferences sPref;
	private boolean Login;
	private Dialog dialog;

	CheckLogin(Activity context, HttpPost contentUrl, String login,
			String pass, SharedPreferences sPref, Dialog dialog) {
		this.context = context;
		this.contentUrl = contentUrl;
		this.login = login;
		this.pass = pass;
		this.sPref = sPref;
		this.Login = false;
		this.dialog = dialog;
	}

	public void check() {
		LoginBender loginIn = new LoginBender(context, contentUrl, login, pass,
				sPref, this);
		loginIn.execute();
	}

	@Override
	public void onLoginActionComplite() {
		this.Login = true;
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	public boolean isLogin() {
		return Login;
	}
}
