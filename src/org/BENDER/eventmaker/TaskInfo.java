package org.BENDER.eventmaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TaskInfo extends Activity implements GetContentListener {

	private TextView taskname;
	private TextView taskstatus;
	private TextView conditonText;
	private TextView inputText;
	private TextView outputText;
	private TextView mistakesText;
	private Button button_code;

	String path;
	String status;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.w("start", "done");
		setContentView(R.layout.taskinfo);

		Log.w("button", "done");
		conditonText = (TextView) findViewById(R.id.condition);
		inputText = (TextView) findViewById(R.id.input_eg);
		outputText = (TextView) findViewById(R.id.output_eg);
		mistakesText = (TextView) findViewById(R.id.mistakes);
		taskname = (TextView) findViewById(R.id.taskname);
		taskstatus = (TextView) findViewById(R.id.taskstatus);
		Log.w("find", "done");
		Intent intent = getIntent();
		path = intent.getStringExtra("path");
		status = intent.getStringExtra("status");
		taskname.setText(path);
		taskstatus.setText(status);
		Log.w("find1", "done");
		if (status.contains("epic win")) {
			button_code = (Button) findViewById(R.id.code_view);
			button_code.setVisibility(View.GONE);
		}
		Log.w("find2", "done");
		BenderContentGet complContent = new BenderContentGet(this,
				"https://my.devclub.com.ua/grading/task/" + path + "/", this);
		complContent.execute();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		BenderContentGet complContent = new BenderContentGet(this,
				"https://my.devclub.com.ua/grading/task/" + path + "/", this);
		complContent.execute();
	}

	public void onCodeClick(View v) {
		Intent i = new Intent(this, Task.class);
		i.putExtra("path", path);
		i.putExtra("status", status);
		startActivity(i);
	}

	@Override
	public void onGetContentComplite(HttpResponse response) {
		try {
			String content = inputStreamToString(
					response.getEntity().getContent()).toString();

			Document doc = Jsoup.parse(content);
			
			Element stat = doc.select("td span").first();
			if (stat != null) {
				taskstatus.setText(stat.text());
			}
			
			conditonText.setText(doc.select("p").first().text());

			boolean hasMistakes = false;
			Elements mistakes = doc.select("h2");
			for (int i = 0; i < mistakes.size(); i++) {
				if (mistakes.get(i).text().equals("Îøèáêè")) {
					hasMistakes = true;
				}
			}
			if (hasMistakes) {
				mistakesText.setText(doc.select("pre").first().text());
			}
			Log.w("", mistakesText.getText().toString());

			TextView[] egs = { inputText, outputText };

			Elements eg = doc.select("pre");
			if (eg.size() < 3 && !hasMistakes) {
				for (int i = 0; i < eg.size(); i++) {
					egs[i].setText(eg.get(i).text());
					Log.v("", eg.get(i).text());
				}
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();

		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line).append("\n");
				;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return total;
	}

}
