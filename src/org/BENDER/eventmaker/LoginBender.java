package org.BENDER.eventmaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

public class LoginBender extends AsyncTask<String, Void, Void> {

	private String strContent;
	private DefaultHttpClient httpclient;
	private Activity context;
	private HttpPost contentUrl;
	private String login;
	private String pass;
	private SharedPreferences sPref;
	private LoginListener listener;
	private ProgressDialog Dialog;

	LoginBender(Activity context, HttpPost contentUrl, String login,
			String pass, SharedPreferences sPref, LoginListener listener) {
		this.context = context;
		this.contentUrl = contentUrl;
		this.login = login;
		this.pass = pass;
		this.sPref = sPref;
		this.listener = listener;
		Dialog = new ProgressDialog(context);
		httpclient = new DefaultHttpClient();
		CookieSyncManager.createInstance(context);
	}

	private StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();

		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return total;
	}

	@Override
	protected void onPreExecute() {
		Dialog.setMessage("Авторизация...");
		Dialog.show();
	}

	@Override
	protected Void doInBackground(String... arg0) {	
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("u", login));
			nameValuePairs.add(new BasicNameValuePair("p", pass));
			contentUrl.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(contentUrl);
			strContent = inputStreamToString(response.getEntity().getContent())
					.toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(Void unused) {
		Dialog.dismiss();
		
		if (!strContent.contains("Выход")) {
			Toast.makeText(context, "Неправильный пароль или имя пользователя !", 1000).show();
			Editor ed = sPref.edit();
			ed.putString("HasData", "no");
			ed.commit();
			
			return;
		}
		
		Cookie cookie = httpclient.getCookieStore().getCookies().get(0);
		
		if (cookie != null) {
			String cookieString = cookie.getName() + "=" + cookie.getValue()
					+ "; domain=" + cookie.getDomain();
			CookieManager.getInstance().setCookie(cookie.getDomain(),
					cookieString);
		}
		CookieSyncManager.getInstance().sync();
		
		listener.onLoginActionComplite();
	}
	
}
