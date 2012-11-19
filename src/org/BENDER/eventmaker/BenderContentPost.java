package org.BENDER.eventmaker;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
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

public class BenderContentPost extends AsyncTask<String, Void, HttpResponse> {

	Activity context;
	HttpPost contentUrl;
	private ProgressDialog Dialog;
	PostContentListener listener;
	String task;
	String content;
	private HttpClient httpclient;
	
	BenderContentPost(Activity context, HttpPost contentUrl, String task,
			String content, PostContentListener listener) {
		this.context = context;
		this.contentUrl = contentUrl;
		this.listener = listener;
		this.task = task;
		this.content = content;
		httpclient = getNewHttpClient();
		CookieSyncManager.createInstance(context);
		Dialog = new ProgressDialog(context);
	}

	protected void onPreExecute() {
		Dialog.setMessage("Отправка данных..");
		Dialog.show();
		Log.w("ShowDilog", "Done");
	}

	@Override
	protected HttpResponse doInBackground(String... params) {
		HttpResponse response = null;
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("task",task));
			nameValuePairs.add(new BasicNameValuePair("test", "0"));
			nameValuePairs.add(new BasicNameValuePair("code", content));
			Log.w("CONTENT",content);
			Log.w("task",task);
			contentUrl.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			String cookie = CookieManager.getInstance().getCookie(
					"my.devclub.com.ua");
			contentUrl.setHeader("Cookie", cookie);
			response = httpclient.execute(contentUrl);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return response;
	}

	protected void onPostExecute(HttpResponse response) {
		Dialog.dismiss();
		Log.w("RESPONSE", response.toString());
		listener.onPostContentComplite(response);
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
