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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PageParser {

    private String HTML;
    public static ProxyObject currProxy = null;
    public static ArrayList<ProxyObject> proxies = new ArrayList<ProxyObject>();

    public HashMap<String, byte[]> getNodes() {
        long start = System.currentTimeMillis();
        System.out.println("[ - Parameter Parser - ]");
        if (!parseParams()) {
            System.out.println("Failed to parse parameters.");
            return null;
        }
        System.out.println("Successfully parsed parameters in : " + (System.currentTimeMillis() - start) + "ms");
        new ClientDownloader(Data.jarLocation);
        HashMap<String, byte[]> clientData = new HashMap<String, byte[]>();
        try {
            JarFile theJar = new JarFile(Data.localJarLocation);
            Enumeration<?> en = theJar.entries();
            while (en.hasMoreElements()) {
                JarEntry entry = (JarEntry) en.nextElement();
                if (entry.getName().startsWith("META"))
                    continue;
                byte[] buffer = new byte[1024];
                int read;
                InputStream is = theJar.getInputStream(entry);
                byte[] allByteData = new byte[0];
                while ((read = is.read(buffer)) != -1) {
                    byte[] tempBuff = new byte[read + allByteData.length];
                    for (int i = 0; i < allByteData.length; ++i)
                        tempBuff[i] = allByteData[i];
                    for (int i = 0; i < read; ++i)
                        tempBuff[i + allByteData.length] = buffer[i];
                    allByteData = tempBuff;
                }
                clientData.put(entry.getName(), allByteData);
            }
            long hash = -1;//getCRC32(Data.localJarLocation);
            System.out.println("Updater ran for : " + (System.currentTimeMillis() - start) + "ms");
            return clientData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getContent(String link) {
        try {
            URL url = new URL(link);
            URLConnection urlCon = url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) urlCon;
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader in = new BufferedReader(isr);
            String myparams = null;
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                myparams += inputLine;
            }
            in.close();
            isr.close();
            is.close();
            connection.disconnect();
            return myparams;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }

    private String remove(String str) {
        return str.replaceAll("\"", "");
    }

    private String getUrl() throws Exception {
        return Data.BASE_LINK + ext("archive=", " ", HTML);
    }

    private String ext(String from, String to, String str1) {
        int p = 0;
        p = str1.indexOf(from, p) + from.length();
        return str1.substring(p, str1.indexOf(to, p));
    }

    private String getNewBaseLink() {
        int[] worlds = {1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 16, 17, 18, 19, 20, 21, 22, 25, 26, 27, 28, 29, 30, 33, 34, 35, 36, 37, 38, 41, 42, 43, 44, 45, 46, 49, 50,
                51, 52, 53, 54, 57, 58, 59, 60, 61, 62, 65, 66, 67, 68, 69, 70, 73, 74, 75, 76, 77, 78, 81, 82, 83, 84, 93, 94};
        return "http://oldschool" + worlds[new Random().nextInt(worlds.length)] + ".runescape.com/";
    }

    private boolean parseParams() {
        try {
            System.out.println("Parsing parameters...");
            Data.BASE_LINK = getNewBaseLink();
            System.out.println("Base Link : " + Data.BASE_LINK);
            HTML = getContent(Data.BASE_LINK);
            Pattern regex = Pattern.compile("<param name=\"?([^\\s]+)\"?\\s+value=\"?([^>]*)\"?>", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher regexMatcher = regex.matcher(HTML);
            while (regexMatcher.find())
                if (!Data.PARAMETERS.containsKey(regexMatcher.group(1))) {
                    Data.PARAMETERS.put(remove(regexMatcher.group(1)), remove(regexMatcher.group(2)));
                }
            if (Data.PARAMETERS.isEmpty()) {
                return false;
            }
            Data.jarLocation = getUrl();
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return false;
    }

    public class InterruptConnection implements Runnable {
        Thread parent;
        URLConnection con;
        long time;

        public InterruptConnection(URLConnection con, long timeout) {
            this.con = con;
            this.time = timeout;
        }

        public void run() {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {

            }
            System.out.println("Connection timed out; disconnecting.");
            ((HttpURLConnection) con).disconnect();
        }
    }
}
