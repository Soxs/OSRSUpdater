/******************************************************
 * Created by Marneus901                                *
 * � 2012-2014                                          *
 * **************************************************** *
 * Access to this source is unauthorized without prior  *
 * authorization from its appropriate author(s).        *
 * You are not permitted to release, nor distribute this* 
 * work without appropriate author(s) authorization.    *
 ********************************************************/
package com.updater.rsloader;

import java.util.HashMap;

public class Data {
	public static String BASE_LINK = "http://world48.runescape.com/";
	public static HashMap<String, String> PARAMETERS = new HashMap<String, String>();
	public static String jarLocation="";
	public static int jarRevision=-1;
	public static String localJarLocation="runescape.jar";
	public static String decryptionKey="";
	public static String ivParameterSpec="";
	public static String logArchiveLocation="";
	public static String jarArchiveLocation="";
	public static String cacheArchiveLocation="";
	public static String modscriptArchiveLocation="";
}
