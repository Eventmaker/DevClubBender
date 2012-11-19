package org.BENDER.eventmaker;

import org.apache.http.HttpResponse;

public interface GetContentListener {
	public void onGetContentComplite(HttpResponse response);
}
