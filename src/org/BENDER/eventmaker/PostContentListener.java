package org.BENDER.eventmaker;

import org.apache.http.HttpResponse;

public interface PostContentListener {
	public void onPostContentComplite(HttpResponse response);
}
