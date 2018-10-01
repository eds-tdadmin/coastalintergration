package com.coastal.dwds.CoastalIntegration.model;

import java.util.Arrays;
import java.util.Collection;

public class CustomBeanFactory {

	private static ReportBean[] data = null;

	public static void setBeanArray(Object newObj) {
		if (data == null) {
			data = new ReportBean[] { (ReportBean) newObj };
		} else {
			data = (ReportBean[]) append(data, newObj);
		}
	}

	public static Object[] getBeanArray() {
		return data;
	}

	public static Collection<?> getBeanCollection() {
		return Arrays.asList(data);
	}

	static <T> T[] append(T[] arr, T element) {
		final int N = arr.length;
		arr = Arrays.copyOf(arr, N + 1);
		arr[N] = element;
		return arr;
	}
}
