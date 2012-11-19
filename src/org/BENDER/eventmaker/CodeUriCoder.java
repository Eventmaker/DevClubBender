package org.BENDER.eventmaker;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public abstract class CodeUriCoder {

	public static String encodeURLComponent(String s) {
		if (s == null) {
			return "";
		}

		final StringBuilder sb = new StringBuilder();

		try {
			for (int i = 0; i < s.length(); i++) {
				final char c = s.charAt(i);

				if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'))
						|| ((c >= '0') && (c <= '9')) || (c == '-')
						|| (c == '.') || (c == '_') || (c == '~')) {
					sb.append(c);
				} else {
					final byte[] bytes = ("" + c).getBytes("UTF-8");

					for (byte b : bytes) {
						sb.append('%');

						int upper = (((int) b) >> 4) & 0xf;
						sb.append(Integer.toHexString(upper).toUpperCase(
								Locale.getDefault()));

						int lower = ((int) b) & 0xf;
						sb.append(Integer.toHexString(lower).toUpperCase(
								Locale.getDefault()));
					}
				}
			}

			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
