package org.BENDER.eventmaker;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class BenderAdapter extends SimpleAdapter {

	public BenderAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);

	}

	@Override
	public void setViewText(TextView v, String text) {
		// метод супер-класса, который вставляет текст
		super.setViewText(v, text);
		// если нужный нам TextView, то разрисовываем
		if (v.getId() == R.id.itemstatus) {
			int i = getItemID(text);
			if (i == 1) {
				v.setTextColor(Color.RED);
			} else if (i == 2) {
				v.setTextColor(Color.LTGRAY);
			} else if (i == 3) {
				v.setTextColor(Color.MAGENTA);
			} else if (i == 4) {
				v.setTextColor(Color.rgb(0, 116, 0));
			} else if (i == 5) {
				v.setTextColor(Color.CYAN);
			} else if (i == 6) {
				v.setTextColor(Color.GREEN);
			}
		}
	}

	int getItemID(String status) {
		if (status.contains("epic win")) {
			return 6;
		}
		if (status.contains("almost win")) {
			return 5;
		}
		if (status.contains("code works")) {
			return 4;
		}
		if (status.contains("review fail")) {
			return 3;
		}
		if (status.contains("code compiles")) {
			return 2;
		}
		if (status.contains("code fail")) {
			return 1;
		}
		return 0;
	}
}
