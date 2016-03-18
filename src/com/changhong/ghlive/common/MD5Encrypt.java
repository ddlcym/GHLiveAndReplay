package com.changhong.ghlive.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* MD5 Encrypt */
/*Author:OscarChang*/
public class MD5Encrypt {

	private static String key = "";
	private static String plainStr = "";

	/* encrypt with key */
	public MD5Encrypt(String plain, String key) {
		plain = plainStr;
		key = key;
	}

	/* encrypt without key */
	public MD5Encrypt(String plain) {
		plain = plainStr;
	}

	public String MD5EncryptExecute() {
		return stringToMD5(plainStr + key);
	}

	/* md5 func */
	public static String stringToMD5(String string) {
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString();
	}
}
