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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.applet.Applet;
import java.io.IOException;
import java.io.InputStream;
import java.security.ProtectionDomain;
import java.util.Hashtable;

public class RSClassLoader extends ClassLoader {

	   static Applet runescapeApplet;
	   static int c;
	   private Object[] d;
	   private static int[] g = new int[128];
	   static int b;
	   static int a;
	   static int f;
	   public static int h;

	   RSClassLoader(Object[] arg0) {
	      this.d = arg0;
	   }
	   static final int a(int arg0, char arg1) {
	      ++b;
	      if(arg0 != -12624) {
	         a(-73, 'W');
	      }

	      return 0 <= arg1 && ~arg1 > ~g.length?g[arg1]:-1;
	   }

	   private final Class<?> findSystemClass(byte arg0, String arg1) throws ClassNotFoundException {
	      ++c;
	      return super.findSystemClass(arg1);
	   }

	   private final Class<?> a(byte[] arg0, ProtectionDomain arg1, int arg2, int arg3, String arg4, byte arg5) throws ClassFormatError {
	      if(arg5 < 123) {
	         return (Class<?>)this.d[0];
	      } else {
	         ++a;
	         return this.defineClass(arg4, arg0, arg2, arg3, arg1);
	      }
	   }

	   @SuppressWarnings("unchecked")
	protected final Class<?> findClass(String arg0) throws ClassNotFoundException {
	      Class<?> var2;
	      int var15;
	      label74: {
	         var15 = h;
	         ++f;
	         Object[] var3 = this.d;
	         Class<?> var4 = (Class<?>)((Hashtable<?, ?>)var3[3]).get(arg0);
	         if(null != var4) {
	            var2 = var4;
	            if(var15 == 0) {
	               break label74;
	            }
	         }

	         byte[] var5;
	         label66: {
	            var5 = (byte[])((byte[])((Hashtable<?, ?>)var3[2]).get(arg0));
	            if(var5 == null) {
	               String var6 = arg0.replace('.', '/') + ".class";
	               InputStream var7 = ((ClassLoader)var3[4]).getResourceAsStream(var6);
	               if(var7 == null) {
	                  var2 = ((RSClassLoader)var3[4]).findSystemClass((byte)-105, arg0);
	                  if(var15 == 0) {
	                     break label74;
	                  }
	               }

	               byte[] var8 = new byte[5242880];
	               int var9 = 0;

	               try {
	                  int var10 = var7.read(var8, var9, 5242880 - var9);

	                  while(var10 != -1) {
	                     var9 += var10;
	                     var10 = var7.read(var8, var9, 5242880 - var9);
	                     if(var15 != 0 || var15 != 0) {
	                        break;
	                     }
	                  }
	               } catch (IOException var18) {
	                  ;
	               }

	               byte[] var20 = new byte[var9];
	               System.arraycopy(var8, 0, var20, 0, var9);
	               Object[] var12 = (Object[])((Object[])var3[0]);
	               byte[] var13 = null;

	               try {
	                  var13 = ((Cipher)var12[0]).doFinal(var20);
	               } catch (BadPaddingException var16) {
	                  ;
	               } catch (IllegalBlockSizeException var17) {
	                  ;
	               }

	               var5 = var13;
	               if(var15 == 0) {
	                  break label66;
	               }
	            }

	            ((Hashtable<?, ?>)var3[2]).remove(arg0);
	         }

	         if(var5 == null) {
	            var2 = ((RSClassLoader)var3[4]).findSystemClass((byte)100, arg0);
	            if(var15 == 0) {
	               break label74;
	            }
	         }

	         Class<?> var19 = ((RSClassLoader)var3[4]).a(var5, (ProtectionDomain)var3[1], 0, var5.length, arg0, (byte)125);
	         var2 = var19;
	         ((Hashtable<String, Class<?>>)var3[3]).put(arg0, var19);
	      }

	      if(true) {
	         ++var15;
	         h = var15;
	      }

	      return var2;
	   }

	   static {
	      int var0;
	      for(var0 = 0; g.length > var0; ++var0) {
	         g[var0] = -1;
	      }

	      for(var0 = 65; 90 >= var0; ++var0) {
	         g[var0] = var0 - 65;
	      }

	      for(var0 = 97; var0 <= 122; ++var0) {
	         g[var0] = var0 - 71;
	      }

	      for(var0 = 48; var0 <= 57; ++var0) {
	         g[var0] = 4 + var0;
	      }

	      int[] var2 = g;
	      g[43] = 62;
	      var2[42] = 62;
	      g[47] = 63;
	      int[] var1 = g;
	      var1[45] = 63;
	   }
}