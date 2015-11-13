package com.xxtv.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @ClassName: BaseMD5Util
 * @Description: MD5加密计算工具类
 * @author Jelly.Liu
 * @date 2014年11月14日 下午6:01:04
 * 
 */
public class BaseMD5Util {
	/**
	 * 获取文件的MD5值
	 * 
	 * @param file
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * 
	 */
	public static String getMd5ByFile(File file) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			return getMd5ByFile(in, file.length());
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (NoSuchAlgorithmException e) {
			throw e;
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					throw e;
				} finally {
					in = null;
				}
			}
		}

	}

	/**
	 * 获取文件的MD5值
	 * 
	 * @param in
	 *            文件输入流
	 * @param size
	 *            文件的大小
	 * @return String
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * 
	 */
	public static String getMd5ByFile(FileInputStream in, long size) throws IOException, NoSuchAlgorithmException {
		if (in == null) {
			return null;
		} else if (size <= 0) {
			return null;
		}
		try {
			MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, size);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			return bi.toString(16);
		} catch (IOException e) {
			throw e;
		} catch (NoSuchAlgorithmException e) {
			throw e;
		}
	}
	 // 转化字符串为十六进制编码
	 public static String toHexString(String s) {
	  String str = "";
	  for (int i = 0; i < s.length(); i++) {
	   int ch = (int) s.charAt(i);
	   String s4 = Integer.toHexString(ch);
	   str = str + s4;
	  }
	  return  str;// 0x表示十六进制
	 }
	 
	 

	/**
	 * main
	 * 
	 * @param args
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		String fileName = "hello";
//		MappedByteBuffer byteBuffer = fileName.
//		MessageDigest md5 = MessageDigest.getInstance("MD5");
//		md5.update(byteBuffer);
//		BigInteger bi = new BigInteger(1, md5.digest());
		String str = DigestUtils.md5Hex(fileName);
		System.out.println(str);
		str = toHexString(str);
		System.out.println(str);
		str = Integer.valueOf(str, 16).toString();
		System.out.println(str);
	}

}
