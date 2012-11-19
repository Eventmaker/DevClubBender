package org.BENDER.eventmaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class TaskList extends Activity implements GetContentListener {

	final static int CODE_FAIL = 1;
	final static int CODE_COMPILES = 2;
	final static int REVIEW_FAIL = 3;
	final static int CODE_WORKS = 4;
	final static int ALMOST_WIN = 5;
	final static int EPIC_WIN = 6;

	final String ATTRIBUTE_TASK = "task";
	final String ATTRIBUTE_STATUS = "status";
	final String ATTRIBUTE_TIME = "time";

	ListView lvSimple;
	SharedPreferences sPref;
	String login;
	String pass;

	ArrayList<TaskItem> taskList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasklist);
		taskList = new ArrayList<TaskItem>();
		sPref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		login = sPref.getString("Login", " ");
		pass = sPref.getString("Password", " ");

		getTasks();
	}

	@Override
	public void onGetContentComplite(HttpResponse response) {
		try {
			String content = inputStreamToString(
					response.getEntity().getContent()).toString();
			Document doc = Jsoup.parse(content);
			Elements table = doc.select("#tasks tr");
			table.remove(0);
			for (Element row : table) {
				String[] item = { "", "", "" };
				int type = 0;
				int id = 0;
				for (int i = 0; i < row.children().size(); i++) {
					item[i] = row.child(i).text();
				}
				if (row.hasClass("optional")) {
					type = 1;
				}
				id = getItemID(item[1]);
				TaskItem taskitem = new TaskItem(item[0], item[1], item[2],
						type, id);
				taskList.add(taskitem);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
				taskList.size());
		Map<String, Object> m;
		for (int i = 0; i < taskList.size(); i++) {
			m = new HashMap<String, Object>();
			m.put(ATTRIBUTE_TASK, taskList.get(i).getTask());
			m.put(ATTRIBUTE_STATUS, taskList.get(i).getStatus());
			m.put(ATTRIBUTE_TIME, taskList.get(i).getTime());
			data.add(m);
		}

		String[] from = { ATTRIBUTE_TASK, ATTRIBUTE_STATUS, ATTRIBUTE_TIME };
		int[] to = { R.id.itemtask, R.id.itemstatus, R.id.itemtime };

		BenderAdapter sAdapter = new BenderAdapter(this, data,
				R.layout.taskitem, from, to);

		lvSimple = (ListView) findViewById(R.id.lvMain);
		lvSimple.setAdapter(sAdapter);

		lvSimple.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.w("LISTENER", "itemClick: position = " + position
						+ ", id = " + id);
			}
		});
	}

	void getTasks() {
		BenderContentGet allTasks = new BenderContentGet(this,
				"https://my.devclub.com.ua/grading/?show=all", this);
		allTasks.execute();
	}

	public void onTaskClick(View v) {
		TextView task = (TextView) v.findViewById(R.id.itemtask);
		TextView status = (TextView) v.findViewById(R.id.itemstatus);
		Log.w("LISTENER", "itemClick: position = " + task.getText());
		Intent i = new Intent(this, TaskInfo.class);
		i.putExtra("path", task.getText().toString());
		i.putExtra("status", status.getText().toString());
		startActivity(i);
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

	int getItemID(String status) {
		if (status.contains("epic win")) {
			return EPIC_WIN;
		}
		if (status.contains("almost win")) {
			return ALMOST_WIN;
		}
		if (status.contains("code works")) {
			return CODE_WORKS;
		}
		if (status.contains("review fail")) {
			return REVIEW_FAIL;
		}
		if (status.contains("code compiles")) {
			return CODE_COMPILES;
		}
		if (status.contains("code fail")) {
			return CODE_FAIL;
		}
		return 0;

	}
}