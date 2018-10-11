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

public class RSException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	String message;

	private RSException(Throwable arg0, String arg1) {
		this.message = arg1;
		this.initCause(arg0);
	}
	static final RSClassLoader getLoader(int arg0, Object[] arg1) {
		return new RSClassLoader(arg1);
	}
	static final String a(String arg0, int arg1, String arg2, String arg3) {
		int var5 = RSClassLoader.h;
		int var4 = arg2.indexOf(arg3);

		String var10000;
		while(true) {
			if(var4 != -1) {
				arg2 = arg2.substring(0, var4) + arg0 + arg2.substring(var4 + arg3.length());
				var10000 = arg2;
				if(var5 != 0) {
					break;
				}

				var4 = arg2.indexOf(arg3, var4 + arg0.length());
				if(var5 == 0) {
					continue;
				}
			}

			var10000 = arg2;
			break;
		}

		return var10000;
	}
	static final RSException a(Throwable arg0, String arg1) {
		RSException var2 = new RSException(arg0, arg1);
		var2.message = var2.message + ' ' + arg1;
		return var2;
	}
}
