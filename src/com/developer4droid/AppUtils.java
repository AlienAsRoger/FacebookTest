package com.developer4droid;

import android.os.Build;

/**
 * AppUtils class
 *
 * @author alien_roger
 * @created at: 01.02.12 7:50
 */
public class AppUtils {

	public static final float MDPI = 1.0f;
	public static final float HDPI = 1.5f;
	public static final float XHDPI = 2.0f;
	private static boolean ENABLE_LOG = true;
	private static final String DAYS = "d";
	private static final String H = "h";
	private static final String M = "m";

	public static final boolean HONEYCOMB_PLUS_API = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	public static final boolean JELLYBEAN_PLUS_API = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;


}
