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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyLoader extends Thread implements Runnable{
    private static final String TEMPLATE = "http://www.hidemyass.com/proxy-list/";
    private static final Pattern PATTERN_PROXY_SECTION = Pattern.compile("<td><span>([\\s\\S]*?)(\\d+)</td>");
    private static final Pattern PATTERN_IGNORE_CLASS = Pattern.compile("\\.(.{4})\\{display:none\\}");
    private String url;
    public ProxyLoader() {
    	for(int i=12;i>=0;--i){
    		if(PageParser.proxies.size()>=100){
    			return;
    		}
    		this.url = TEMPLATE.concat(Integer.toString(i));
    		loadProxies();
    		if(i==0)
    			i++;
    	}
    }
    public void loadProxies() {
        try {
            final URL url = new URL(this.url);
            final BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            final StringBuilder dataBuilder = new StringBuilder();
            String tempString;
            while ((tempString = br.readLine()) != null) {
                dataBuilder.append(tempString);
            }
            String data = dataBuilder.toString();
            final Matcher toIgnore = PATTERN_IGNORE_CLASS.matcher(data);
            while (toIgnore.find()) {
                data = data.replaceAll("(<(div|span) class=\"" + toIgnore.group(1) + "\">(.*?)</(div|span)>)", "");
            }
            Matcher m = PATTERN_PROXY_SECTION.matcher(data);
            int i=0;
            while (m.find()) {
                String port = m.group(2).trim().replaceAll("[^0-9]", "");
                String ip = m.group(1);
                ip = ip.substring(ip.indexOf("<span")).replaceAll("<span class=\".*?\">", "").replaceAll("<(div|span) style=\"display:none\">(.*?)</(div|span)>", "").replaceAll("<.*?>", "").trim();
                if (ip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
                    PageParser.proxies.add(new ProxyObject(ip, Integer.parseInt(port)));
                    System.out.println("Loaded proxy : "+ip+":"+port);
                    i++;
                }
            }
            System.out.println("Loaded "+i+" proxies for use...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
