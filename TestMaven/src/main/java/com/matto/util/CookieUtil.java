package com.matto.util;

import javax.servlet.http.Cookie;

public class CookieUtil {

	public static String getCookieValue(Cookie[] cookie, String key) {
		String value = "";
		for (int i = 0; i < cookie.length; i++) {
			Cookie cook = cookie[i];
			if (cook.getName().equalsIgnoreCase(key)) {
				value = cook.getValue().toString();
			}
		}
		return value;
	}
}
