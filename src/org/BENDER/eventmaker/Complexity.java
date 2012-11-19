package org.BENDER.eventmaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Complexity extends Activity implements OnClickListener, GetContentListener {

	private TextView complexContent;
	private TextView complexAnswer;
	private String answer;
	private Button o1;
	private Button ologN;
	private Button olog2N;
	private Button oN;
	private Button oNlogN;
	private Button oN2;
	private Button bMore;
	private LinearLayout answerBlok;
	private TextView result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.complexity);
		CookieSyncManager.createInstance(this);

		complexContent = (TextView) findViewById(R.id.complexity_content);
		complexAnswer = (TextView) findViewById(R.id.complexity_answer);
		o1 = (Button) findViewById(R.id.bOne);
		ologN = (Button) findViewById(R.id.blogN);
		olog2N = (Button) findViewById(R.id.blog2N);
		oN = (Button) findViewById(R.id.bN);
		oNlogN = (Button) findViewById(R.id.bNlogN);
		oN2 = (Button) findViewById(R.id.bN2);
		bMore = (Button) findViewById(R.id.bMore);
		answerBlok = (LinearLayout) findViewById(R.id.answerBlok);
		result = ((TextView) findViewById(R.id.result));

		fill();

		o1.setOnClickListener(this);
		ologN.setOnClickListener(this);
		olog2N.setOnClickListener(this);
		oN.setOnClickListener(this);
		oNlogN.setOnClickListener(this);
		oN2.setOnClickListener(this);
		bMore.setOnClickListener(this);

	}

	private StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();

		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line).append("\n");;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return total;
	}
	
	@Override
	public void onGetContentComplite(HttpResponse response) {
		String content = "";
		try {
			content = inputStreamToString(response.getEntity().getContent())
					.toString();
		} catch (IllegalStateException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Document doc = Jsoup.parse(content);
		Elements table = doc.select("div pre");

		Elements answerWhy = doc.select("aside p");
		String why = answerWhy.get(1).text().substring(18);

		Elements answerCheck = doc.select("aside strong");
		answer = answerCheck.text();
		answer = answer.substring(18);
		if (answer.contains("2")) {
			if (answer.contains("log")) {
				answer = "O(log2N)";
				why = why.substring(10);
			} else {
				answer = "O(N2)";
				why = why.substring(8);
			}
		}
		complexContent.setText(table.text());
		complexAnswer.setText(why);
		answerBlok.setVisibility(View.INVISIBLE);
		setButtonsClickable(true);
		
	}
	
	private void fill() {
		BenderContentGet complContent = new BenderContentGet(this,
				"https://my.devclub.com.ua/grading/complexity/",this);
		complContent.execute();
		
	}

	public void onHome(View v) {
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bOne: {
			if (answer.contains("O(1)")) {
				setWin();
			} else {
				setFail();
			}
		}
			break;
		case R.id.blogN: {
			if (answer.contains("O(logN)")) {
				setWin();
			} else {
				setFail();
			}
		}
			break;
		case R.id.blog2N: {
			if (answer.contains("O(log2N)")) {
				setWin();
			} else {
				setFail();
			}
		}
			break;
		case R.id.bN: {
			if (answer.contains("O(N)")) {
				setWin();
			} else {
				setFail();
			}
		}
			break;
		case R.id.bNlogN: {
			if (answer.contains("O(NlogN)")) {
				setWin();
			} else {
				setFail();
			}
		}
			break;
		case R.id.bN2: {
			if (answer.contains("O(N2)")) {
				setWin();
			} else {
				setFail();
			}
		}
			break;
		case R.id.bMore: {
			answerBlok.setVisibility(View.INVISIBLE);
			fill();
		}
			break;
		}

	}

	public void setWin() {
		result.setText("EPIC WIN");
		result.setTextColor(Color.rgb(0, 255, 0));
		setButtonsClickable(false);
		answerBlok.setVisibility(View.VISIBLE);
	}

	public void setFail() {
		result.setText("EPIC FAIL");
		result.setTextColor(Color.rgb(255, 0, 0));
		setButtonsClickable(false);
		answerBlok.setVisibility(View.VISIBLE);
	}

	public void setButtonsClickable(boolean state) {
		o1.setClickable(state);
		ologN.setClickable(state);
		olog2N.setClickable(state);
		oN.setClickable(state);
		oNlogN.setClickable(state);
		oN2.setClickable(state);
	}
}
