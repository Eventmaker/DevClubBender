package org.BENDER.eventmaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Task extends Activity implements GetContentListener,
		PostContentListener {

	private WebView wv;
	private String mRezContent;
	private ProgressDialog progressDialog;
	private EditText mNewFileName;
	private TextView taskname;
	private TextView taskstatus;
	private File mEdited;
	String path;
	String status;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage("Loading sourse");
		progressDialog.setCancelable(false);

		setContentView(R.layout.taskcode);
		Intent intent = getIntent();

		path = intent.getStringExtra("path");
		status = intent.getStringExtra("status");

		taskname = (TextView) findViewById(R.id.code_task);
		taskname.setText(path);

		taskstatus = (TextView) findViewById(R.id.code_status);
		taskstatus.setText(status);

		mEdited = null;

		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File extDir = Environment.getExternalStorageDirectory();
			mEdited = new File(extDir, "Bender/" + path + "/" + path + ".c");
		} else {
			Toast.makeText(getApplicationContext(), "Карта памяти не доступна",
					Toast.LENGTH_LONG).show();
		}
		if (!mEdited.exists())
			mEdited.getParentFile().mkdirs();

		BenderContentGet complContent = new BenderContentGet(this,
				"https://my.devclub.com.ua/grading/task/" + path + "/", this);
		complContent.execute();
	}

	public void onSend(View v) {
		HttpPost contentUrl = new HttpPost(
				"https://my.devclub.com.ua/grading/submit/");
		BenderContentPost postCode = new BenderContentPost(this, contentUrl,
				path, mRezContent, this);
		postCode.execute();
	}

	private StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();

		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
				total.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return total;
	}

	@Override
	public void onPostContentComplite(HttpResponse response) {
		BenderContentGet complContent = new BenderContentGet(this,
				"https://my.devclub.com.ua/grading/task/" + path + "/", this);
		complContent.execute();
		Log.w("POSTCOMPLITE", "gg");
	}

	@Override
	public void onGetContentComplite(HttpResponse response) {
		String content = "";

		try {
			content = inputStreamToString(response.getEntity().getContent())
					.toString();

			Document doc = Jsoup.parse(content);
			Element stat = doc.select("td span").first();
			if (stat != null) {
				taskstatus.setText(stat.text());
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String str = "<textarea id=\"code\" name=\"code\" cols=\"80\" rows=\"20\">";
		int start = content.indexOf(str) + str.length();
		int end = content.indexOf("</textarea>");
		mRezContent = content.substring(start, end);

		wv = (WebView) findViewById(R.id.content);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.addJavascriptInterface(new JavascriptInterface(), "Android");
		wv.getSettings().setSupportZoom(true);
		wv.getSettings().setBuiltInZoomControls(true);
		wv.getSettings().setLoadWithOverviewMode(true);
		wv.getSettings().setUseWideViewPort(true);
		wv.getSettings().setPluginsEnabled(true);
		wv.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		wv.setWebChromeClient(new FileWebClient());

		wv.loadUrl("file:///android_asset/editor.html");
		wv.requestFocusFromTouch();

		progressDialog.setProgress(0);
		progressDialog.show();
		Log.w("GETCOMPLITE", "gg");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.editormenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_as:
			saveToFile(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void saveToFile(boolean needNewName) {
		if (needNewName) {
			mNewFileName = new EditText(this);
			mNewFileName.setText(mEdited.getName());
			mNewFileName.setSingleLine();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.msgnewfilename)
					.setCancelable(false)
					.setView(mNewFileName)
					.setPositiveButton(R.string.saveas,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									String newFname = mNewFileName.getText()
											.toString();
									mEdited = new File(mEdited.getParent()
											+ "/" + newFname);
									saveToFile(false);
								}
							})
					.setNegativeButton(R.string.btncancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
			builder.create().show();
		} else {
			FileWriter out = null;
			try {
				mEdited.createNewFile();
				out = new FileWriter(mEdited);
				out.write(mRezContent);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			} finally {
				try {
					if (out != null) {
						out.flush();
						out.close();
					}
				} catch (IOException ee) {
				}
			}
		}
	}

	private class JavascriptInterface {

		@SuppressWarnings("unused")
		public void contOut(String html) {
			mRezContent = html;
		}
	}

	private class FileWebClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress < 100) {
				progressDialog.setProgress(newProgress);
			} else {
				progressDialog.dismiss();
				mRezContent = mRezContent.replace("&apos;", "'");
				mRezContent = mRezContent.replace("&quot;", "\"");
				mRezContent = mRezContent.replace("&amp;", "&");
				mRezContent = mRezContent.replace("&lt;", "<");
				mRezContent = mRezContent.replace("&gt;", ">");
				mRezContent = mRezContent.replace("&#039;", "'");

				wv.loadUrl("javascript:setContent('"
						+ CodeUriCoder.encodeURLComponent(mRezContent) + "');");
			}
		}
	}

}
