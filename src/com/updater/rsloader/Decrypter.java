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
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Unpacker;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.GZIPInputStream;

public class Decrypter {
	static int destroyInvokeCount;
	static int paintInvokeCount;
	static int stopInvokeCount;
	static int methodInvokeCount;
	static int updateInvokeCount;
	static int startInvokeCount;
	static int b;
	static int h;
	static int k;
	private static Object[] d;
	public static boolean f;
	private static final byte[] decypher(Object[] arg0, byte arg1, byte[] arg2) {
		++h;
		byte[] var3 = null;
		if(arg1 != 87) {
			decypher((Object[])((Object[])arg0[17]), (byte)-18, (byte[])null);
		}

		try {
			var3 = ((Cipher)arg0[0]).doFinal(arg2);
		} catch (BadPaddingException var5) {
			;
		} catch (IllegalBlockSizeException var6) {
			;
		}

		return var3;
	}
	public Decrypter(){
		long start = System.currentTimeMillis();
		System.out.println("[ - Client Decrypter - ]");
		int var27 = RSClassLoader.h;
		try {
			byte[] var2;
			byte[] var7;
			int var8;
			int var9;
			int var11;
			int var12;
			int var13;
			int var14;
			label245: {
				++k;
				String var3 = Data.PARAMETERS.get("0");
				int var4 = var3.length();
				if(var4 == 0) {
					var2 = new byte[0];
					if(var27 == 0) {
						break label245;
					}

					f = !f;
				}

				int var6;
				label234: {
					int var5 = -4 & 3 + var4;
					var6 = var5 / 4 * 3;
					if(var5 - 2 < var4 && ~RSClassLoader.a(-12624, var3.charAt(var5 - 2)) != 0) {
						if(var5 - 1 < var4 && -1 != RSClassLoader.a(-12624, var3.charAt(var5 - 1))) {
							break label234;
						}

						--var6;
						if(var27 == 0) {
							break label234;
						}
					}

					var6 -= 2;
				}

				var7 = new byte[var6];
				var8 = 0;
				var2 = var7;
				var9 = var3.length();
				int var10 = 0;

				while(var10 < var9) {
					var11 = RSClassLoader.a(-12624, var3.charAt(var10));
					var12 = var9 <= var10 + 1?-1:RSClassLoader.a(-12624, var3.charAt(var10 + 1));
					var13 = var10 + 2 < var9?RSClassLoader.a(-12624, var3.charAt(2 + var10)):-1;
					var14 = 3 + var10 < var9?RSClassLoader.a(-12624, var3.charAt(3 + var10)):-1;
					var7[var8++] = (byte)(var11 << 2 | var12 >>> 4);
					if(var13 == -1) {
						break;
					}

					var7[var8++] = (byte)((var12 & 15) << 4 | var13 >>> 2);
					if(~var14 == 0) {
						break;
					}

					var7[var8++] = (byte)((3 & var13) << 6 | var14);
					var10 += 4;
					if(var27 != 0) {
						break;
					}
				}
			}

			int streamIndex;
			int bytesRead;
			byte[] var41;
			label248: {
				String var43 = Data.PARAMETERS.get("-1");
				int var42 = var43.length();
				if(var42 == 0) {
					var41 = new byte[0];
					if(var27 == 0) {
						break label248;
					}
				}

				label270: {
					var8 = -4 & 3 + var42;
					var9 = 3 * (var8 / 4);
					if(var42 <= var8 - 2 || ~RSClassLoader.a(-12624, var43.charAt(var8 - 2)) == 0) {
						var9 -= 2;
						if(var27 == 0) {
							break label270;
						}
					}

					if(var42 <= var8 - 1 || -1 == RSClassLoader.a(-12624, var43.charAt(var8 - 1))) {
						--var9;
					}
				}

				byte[] var46 = new byte[var9];
				var11 = 0;
				var41 = var46;
				var12 = var43.length();
				var13 = 0;

				while(var12 > var13) {
					var14 = RSClassLoader.a(-12624, var43.charAt(var13));
					streamIndex = 1 + var13 >= var12?-1:RSClassLoader.a(-12624, var43.charAt(1 + var13));
					bytesRead = var12 > var13 + 2?RSClassLoader.a(-12624, var43.charAt(var13 + 2)):-1;
					int var17 = var12 <= var13 + 3?-1:RSClassLoader.a(-12624, var43.charAt(3 + var13));
					var46[var11++] = (byte)(streamIndex >>> 4 | var14 << 2);
					if(bytesRead==-1) {
						break;
					}

					var46[var11++] = (byte)(240 & streamIndex << 4 | bytesRead >>> 2);
					if(~var17 == 0 && var27 == 0) {
						break;
					}

					var46[var11++] = (byte)(bytesRead << 6 & 192 | var17);
					var13 += 4;
					if(var27 != 0) {
						break;
					}
				}
			}

			var7 = var41;
			Object[] var44 = new Object[1];
			SecretKeySpec var45 = new SecretKeySpec(var2, "AES");

			try {
				var44[0] = Cipher.getInstance("AES/CBC/PKCS5Padding");
				((Cipher)var44[0]).init(2, var45, new IvParameterSpec(var7));
			} catch (NoSuchPaddingException var34) {
				throw new RuntimeException(var34);
			} catch (NoSuchAlgorithmException var35) {
				throw new RuntimeException(var35);
			} catch (InvalidKeyException var36) {
				throw new RuntimeException(var36);
			} catch (InvalidAlgorithmParameterException var37) {
				throw new RuntimeException(var37);
			}

			d = var44;
			Object[] var48 = new Object[5];
			Object[] var47 = d;
			var48[4] = RSException.getLoader(-26488, var48);
			var48[0] = var47;
			var48[3] = new Hashtable<Object, Object>();
			var48[2] = new Hashtable<Object, Object>();
			Permissions var49 = new Permissions();
			var49.add(new AllPermission());
			var48[1] = new ProtectionDomain(new CodeSource((URL)null, (Certificate[])null), var49);
			InputStream encryptedClient=null;
			try{
				JarFile theJar = new JarFile("temp.jar");
				Enumeration<?> en = theJar.entries();
				while (en.hasMoreElements()) {
					JarEntry entry = (JarEntry) en.nextElement();
					if (entry.getName().endsWith(".gz")) {
						encryptedClient = theJar.getInputStream(entry);	
						System.out.println("Found encrypted client : inner.pack.gz.");
						break;
					}
				}
			}
			catch(Exception e){
				System.out.println("Error when finding encrypted client.");
				return;
			}
			if(encryptedClient == null) {
				System.out.println("Failed to find inner.pack.gz.");
				return;
			}
			
			System.out.println("Decrypting runescape client...");
			byte[] tempData = new byte[5242880];
			streamIndex = 0;

			try {
				bytesRead = encryptedClient.read(tempData, streamIndex, 5242880 - streamIndex);

				while(bytesRead!=-1) {
					streamIndex += bytesRead;
					bytesRead = encryptedClient.read(tempData, streamIndex, 5242880 - streamIndex);
					if(var27 != 0) {
						break;
					}
				}
			} catch (Exception var39) {
				var39.printStackTrace();
			}
			
			byte[] var53 = new byte[streamIndex];
			System.arraycopy(tempData, 0, var53, 0, streamIndex);
			
			byte[] decipheredData = decypher(d, (byte)87, var53);
			Unpacker jarUnpacker = Pack200.newUnpacker();
			encryptedClient.close();
			File file = new File("runescape.jar");
			FileOutputStream outputStream;
			try {
				outputStream = new FileOutputStream(file);
				JarOutputStream decryptedClient = new JarOutputStream(outputStream);
				GZIPInputStream zipClient = new GZIPInputStream(new ByteArrayInputStream(decipheredData));
				jarUnpacker.unpack(zipClient, decryptedClient);
				System.out.println("Decrypted and saved client in : "+(System.currentTimeMillis()-start)+"ms");
				decryptedClient.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			new File("temp.jar").delete();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public long getCRC32(String file){
		try{
			FileInputStream fis = new FileInputStream(file);
			CRC32 crc = new CRC32();
			CheckedInputStream cis = new CheckedInputStream(fis, crc);
			byte[] buffer = new byte[(int) new File(file).length()];
			cis.read(buffer);
			cis.close();
			fis.close();
			return cis.getChecksum().getValue();
		}
		catch(Exception e){
			
		}
		return -1;
	}
	public static String byteArrayToHexString(final byte[] b) {
		final StringBuilder s = new StringBuilder(b.length * 2);
		for (final byte aB : b) {
			s.append(Integer.toString((aB & 0xff) + 0x100, 16).substring(1));
		}
		return s.toString();
	}
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
}
