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

public class ProxyObject{
	String ip;
	public int port;
	public ProxyObject(String s, int i){
		ip=s;
		port=i;
	}
	public int getPort(){
		return port;
	}
	public String getIP(){
		return ip;
	}
}