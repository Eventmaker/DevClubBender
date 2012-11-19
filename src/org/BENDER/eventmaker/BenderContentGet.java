package org.BENDER.eventmaker;

import java.io.IOException;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class BenderContentGet extends AsyncTask<String, Void, HttpResponse> {

	Activity context;
	String url;
	private ProgressDialog Dialog;
	GetContentListener listener;

	BenderContentGet(Activity context, String url, GetContentListener listener) {
		this.context = context;
		this.url = url;
		this.listener = listener;
		CookieSyncManager.createInstance(context);
		Dialog = new ProgressDialog(context);
	}

	protected void onPreExecute() {
		Dialog.setMessage("Загрузка данных..");
		Dialog.show();
		Log.w("ShowDilog", "Done");
	}

	@Override
	protected HttpResponse doInBackground(String... params) {
		HttpResponse response = null;
		HttpGet getMethod = new HttpGet(url);
		HttpClient httpclient = getNewHttpClient();
		String keyValueSets = CookieManager.getInstance().getCookie(
				"my.devclub.com.ua");
		getMethod.setHeader("Cookie", keyValueSets);
		try {
			response = httpclient.execute(getMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	protected void onPostExecute(HttpResponse response) {
		Dialog.dismiss();
		listener.onGetContentComplite(response);
	}

	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUserAgent(params, "Android/WebKit");
			HttpConnectionParams.setConnectionTimeout(params, 30000);
			HttpConnectionParams.setSoTimeout(params, 30000);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			DefaultHttpClient httpclient = new DefaultHttpClient(ccm, params);

			return httpclient;
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

}
