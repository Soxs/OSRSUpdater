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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ClientDownloader {

	public ClientDownloader(String jarLocation){
		long start = System.currentTimeMillis();
		System.out.println("[ - Client Downloader - ]");
		if(!jarLocation.equals("")){
			System.out.println("JAR Location : "+jarLocation);
			System.out.println("Downloading runescape client... ");
			if(downloadFile(jarLocation)){
				System.out.println("Succesfully downloaded client in : "+(System.currentTimeMillis()-start)+"ms");
			}
			else
				System.out.println("Failed to download client.");
		}
		else
			System.out.println("Invalid JAR Location!");
		
	}
	private boolean downloadFile(final String link) {
		try {
			URL url = new URL(link);
			String referer = url.toExternalForm();
            URLConnection uc = url.openConnection();
            uc.addRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
            uc.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            uc.addRequestProperty("Accept-Encoding", "gzip,deflate");
            uc.addRequestProperty("Accept-Language", "en-gb,en;q=0.5");
            uc.addRequestProperty("Connection", "keep-alive");
            uc.addRequestProperty("Host", "www.runescape.com");
            uc.addRequestProperty("Keep-Alive", "300");
            if (referer != null)
                uc.addRequestProperty("Referer", referer);
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.0.6) Gecko/20060728 Firefox/1.5.0.6");		

			BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
			FileOutputStream fos = new FileOutputStream("runescape.jar");
			BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
			byte[] data = new byte[1024];
			int x = 0;
			while((x=in.read(data, 0, 1024))>=0) {
				bout.write(data, 0, x);
			}
			bout.close();
			in.close();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
