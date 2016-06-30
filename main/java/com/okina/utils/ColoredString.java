package com.okina.utils;

public class ColoredString {

	public String str;
	public int color;

	public ColoredString(String str, int color) {
		this.str = str;
		this.color = color;
	}
	public boolean isEmpty() {
		return str == null || str.isEmpty();
	}

}
