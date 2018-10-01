/**

 * Copyright (c) {2011} {meter@rbtsb.com} { individual contributors as indicated
 * by the @authors tag}. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Private License v1.0
 * which accompanies this distribution, and is available at http://www.rbtsb.com
 */
package com.coastal.dwds.CoastalIntegration.common;

import java.util.UUID;

/**
 * StringUtils.
 * 
 * @author mujoko
 */
public final class StringUtils {

	/**
	 * convert null to empty string
	 * 
	 * @param arg
	 * @return
	 */
	public static String fixNull(final String arg) {
		return arg == null ? "" : arg;
	}

	/** construct log object to debug. */
	// private static final Logger log = Logger.getLogger(StringUtils.class);

	/**
	 * FormatMessage.
	 * 
	 * @param msg
	 *            to search for
	 * @param replacement
	 *            to search for
	 * @return string if found
	 */
	public static String formatMessage(final String msg, final String[] replacement) {
		String formatedMsgStr = msg;
		if (msg == null) {
			return "Could not get proper message to display. " + "Contact the Administrator.";
		}
		for (int i = 0; i < replacement.length; i++) {
			String pattern = "\\{" + i + "\\}";
			if (replacement[i] != null) {
				formatedMsgStr = msg.replaceAll(pattern, replacement[i]);
			} else {
				formatedMsgStr = msg.replaceAll(pattern, "----");
			}
		}
		return formatedMsgStr;
	}

	/**
	 * @return UUID String
	 */
	public static String generateUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * @param classObj
	 *            class object
	 * @return class name
	 */
	public static String getClassName(final String classObj) {
		String className = null;
		if (classObj != null) {
			if (classObj.lastIndexOf('.') > 0) {
				className = classObj.substring(classObj.lastIndexOf('.') + 1);
				className = className.replace('$', '.'); // Map.Entry
			}
		}
		return className;
	}

	/**
	 * <p>
	 * This function to trim spaces if length is equals 0 it return null value.
	 * <br>
	 * <b>Returns: String </b>
	 * 
	 * @param myValue
	 *            string value
	 * @return string
	 */
	public static String getNullIfEmpty(final String myValue) {
		String value = myValue;
		if (value != null && value.length() > 0) {
			value = value.trim();
			if (value.length() > 0) {
				return value;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * @param val
	 *            get the string if its null val
	 * @return empty String if null
	 */
	public static String getStringIfNull(final String val) {
		if (val != null) {
			return val.trim();
		} else {
			return "";
		}
	}

	/**
	 * @param arg
	 *            .
	 * @return boolean
	 */
	public static boolean isEmpty(final String arg) {
		return StringUtils.isEmpty(arg);
	}

	/**
	 * @param arg
	 *            .
	 * @return boolean
	 */
	public static boolean isNotEmpty(final String arg) {
		return StringUtils.isNotEmpty(arg);
	}

	/**
	 * Right Pad the given string with spaces.
	 * 
	 * @param value
	 *            value
	 * @param length
	 *            length
	 * @return string string
	 */
	public static String leftPadWithZeros(final String value, final int length) {

		StringBuilder sb = new StringBuilder();
		for (int toprepend = length - value.length(); toprepend > 0; toprepend--) {
			sb.append("0");
		}
		sb.append(value);
		return sb.toString();
	}

	/**
	 * Right Pad the given string with spaces.
	 * 
	 * @param value
	 *            value
	 * @param length
	 *            length
	 * @return string string
	 */
	public static String padWithSpaces(final String value, final int length) {

		StringBuffer buf = new StringBuffer(value);

		if (value.length() < length) {
			for (int i = 0; i < (length - value.length()); i++) {
				buf.append(" ");
			}
		}

		return buf.toString();
	}

	/**
	 * Right Pad the given string with spaces.
	 * 
	 * @param value
	 *            value
	 * @param length
	 *            length
	 * @return string string
	 */
	public static String padWithZeros(final String value, final int length) {

		StringBuffer buf = new StringBuffer();

		if (value.length() < length) {
			for (int i = 0; i < (length - value.length()); i++) {
				buf.append("0");
			}
		}

		buf.append(value);
		return buf.toString();
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String toUpper(String str) {
		if (str == null) {
			return null;
		}
		return str.toUpperCase();
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static String trim(String value) {
		if (value == null) {
			return value;
		}

		return value.trim();
	}

	/**
	 * Constructor for utility class.
	 */
	private StringUtils() {
	}
}
